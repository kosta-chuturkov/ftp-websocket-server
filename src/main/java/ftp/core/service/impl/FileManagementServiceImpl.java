package ftp.core.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import ftp.core.config.ApplicationConfig;
import ftp.core.config.FtpConfigurationProperties;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.dto.DeletedFileDto;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.ResponseModelAdapter;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.face.StorageService;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.DtoUtil;
import ftp.core.util.ServerUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ftp.core.util.ServerUtil.getProtocol;

@Service("fileManagementService")
public class FileManagementServiceImpl implements FileManagementService {
    private static final Logger logger = Logger.getLogger(FileManagementServiceImpl.class);

    final Set<String> ALLOWED_EXTENTIONS = Sets.newHashSet("jpg", "JPG");
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private EventService eventService;
    @Resource
    private Gson gson;
    @Resource
    private JsonParser jsonParser;
    @Resource
    private StorageService storageService;
    private FtpConfigurationProperties ftpConfigurationProperties;

    @Autowired
    public FileManagementServiceImpl(FtpConfigurationProperties ftpConfigurationProperties) {

        this.ftpConfigurationProperties = ftpConfigurationProperties;
    }

    @Override
    public String updateProfilePicture(final HttpServletRequest request,
                                       final MultipartFile file) throws IOException {

        final String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
        final String extension = FilenameUtils.getExtension(fileName);
        checkFileExtention(extension);
        String nickName = User.getCurrent().getNickName();
        final String serverFileName = nickName + "." + extension;
        this.storageService.storeProfilePicture(file.getInputStream(), serverFileName);
        org.springframework.core.io.Resource profilePicture = this.storageService.loadProfilePicture(nickName);
        createImageThumbnail(profilePicture.getFile(), 50, 50);
        profilePicture.getInputStream().close();
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imageUrl", getProfilePicUrl(nickName, ServerUtil.getServerContextAddress(request)));
        return jsonObject.toString();
    }

    @Override
    public String uploadFile(final HttpServletRequest request,
                             final MultipartFile file,
                             final String userNickNames) throws IOException {

        final int port = request.getServerPort();
        final String host = request.getServerName();
        final String contextPath = request.getContextPath();
        final String serverContextAddress = getProtocol(request) + host + ":" + port + contextPath
                + APIAliases.DOWNLOAD_FILE_ALIAS;
        final Long token = User.getCurrent().getToken();
        final String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
        final long currentTime = System.currentTimeMillis();
        final String tempFileName = new Long(currentTime).toString();
        final String serverFileName = tempFileName + "_" + fileName;
        final String deleteHash = ServerUtil.hash(ServerUtil.hash(serverFileName + token) + ServerConstants.DELETE_SALT);
        final String downloadHash = ServerUtil.hash((serverFileName + token) + ServerConstants.DOWNLOAD_SALT);

        final Set<String> users = getFileSharedUsersAsSet(userNickNames);
        this.fileService.createFileRecord(fileName, currentTime, users, file.getSize(),
                deleteHash, downloadHash);
        this.storageService.store(file.getInputStream(), serverFileName, User.getCurrent().getEmail());
        return buildResponseObject(file, serverContextAddress, fileName, deleteHash, downloadHash).toString();

    }


    private void createImageThumbnail(java.io.File targetFile, int width, int height) throws IOException {
        Thumbnails.of(targetFile)
                .size(width, height)
                .outputFormat("jpg")
                .toFiles(Rename.NO_CHANGE);
    }

    private void checkFileExtention(String extension) {
        if (!this.ALLOWED_EXTENTIONS.contains(extension)) {
            throw new FtpServerException("Image expected...");
        }
    }

    private JSONObject buildResponseObject(MultipartFile file, String serverContextAddress, String fileName, String deleteHash, String downloadHash) {
        JsonFileDto dtoWrapper = new JsonFileDto.Builder()
                .withName(StringEscapeUtils.escapeHtml(fileName))
                .withSize(Long.toString(file.getSize()))
                .withUrl((serverContextAddress + downloadHash))
                .withDeleteUrl((serverContextAddress + ServerConstants.DELETE_ALIAS + deleteHash))
                .withDeleteType("GET")
                .build();

        return ServerUtil.geAstJsonObject(new ResponseModelAdapter.Builder().withBaseFileDto(dtoWrapper).build());
    }

    private Set<String> getFileSharedUsersAsSet(final String userNickNames) {
        final Set<String> users = Sets.newHashSet();
        if (userNickNames.isEmpty()) return users;
        final JsonElement elem = this.jsonParser.parse(userNickNames);
        final JsonArray asJsonArray = elem.getAsJsonArray();
        asJsonArray
                .forEach(jsonElement -> {
                    JsonObject asJsonObject = jsonElement.getAsJsonObject();
                    if (asJsonObject != null && asJsonObject.get("name") != null) {
                        users.add(StringEscapeUtils.escapeSql(asJsonObject.get("name").getAsString()));
                    } else {
                        throw new IllegalArgumentException("Expected parameter name.");
                    }
                });
        return users;
    }

    @Override
    public void deleteFiles(final HttpServletResponse response, final String deleteHash) {
        final User current = User.getCurrent();
        final String nickName = current.getNickName();
        final File findByDeleteHash = getFile(deleteHash, nickName);
        final String downloadHash = findByDeleteHash.getDownloadHash();
        final Set<String> sharedWithUsers = findByDeleteHash.getSharedWithUsers();
        final List<String> usersToBeNotifiedFileDeleted = Lists.newArrayList(sharedWithUsers);
        usersToBeNotifiedFileDeleted.add(current.getNickName());
        final long fileSize = findByDeleteHash.getFileSize();
        final String name = findByDeleteHash.getName();
        final Date timestamp = findByDeleteHash.getTimestamp();

        this.fileService.delete(findByDeleteHash.getId());

        current.setRemainingStorage(current.getRemainingStorage() + fileSize);
        this.userService.saveAndFlush(current);
        final User updatedUser = this.userService.findOne(current.getId());
        final String storageInfo = FileUtils.byteCountToDisplaySize(updatedUser.getRemainingStorage()) + " left from "
                + FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT) + ".";
        ServerUtil.sendOkResponce(response, name, storageInfo);
        this.storageService.deleteResource(getFilenameWithTimestamp(timestamp, name), updatedUser.getEmail());
        this.eventService.fireRemovedFileEvent(usersToBeNotifiedFileDeleted, new DeletedFileDto(downloadHash));
    }

    private File getFile(String deleteHash, String nickName) {
        final File findByDeleteHash = this.fileService.findByDeleteHash(deleteHash, nickName);
        if (findByDeleteHash == null) {
            throw new RuntimeException("File does not exist.");
        }
        return findByDeleteHash;
    }

    @Override
    public void sendProfilePicture(final HttpServletResponse response, String userName) {
        org.springframework.core.io.Resource resource = this.storageService.loadProfilePicture(userName);
        sendResourceByName(response, resource);
    }

    @Override
    public void downloadFile(String downloadHash, final HttpServletResponse response) {
        final User current = User.getCurrent();
        final File fileByDownloadHash = getFile(downloadHash);
        final Date timestamp = fileByDownloadHash.getTimestamp();
        final String fileName = fileByDownloadHash.getName();
        final File.FileType fileType = fileByDownloadHash.getFileType();
        String fileLocationFolder = getFolderNameByFileType(current, fileByDownloadHash, fileType);
        try {
            org.springframework.core.io.Resource resource = this.storageService.loadAsResource(getFilenameWithTimestamp(timestamp, fileName), fileLocationFolder);
            sendResourceByName(response, resource);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilenameWithTimestamp(Date timestamp, String fileName) {
        return timestamp.getTime() + "_" + fileName;
    }

    private File getFile(String downloadHash) {
        final File fileByDownloadHash = this.fileService.getFileByDownloadHash(downloadHash);
        if (fileByDownloadHash == null) {
            throw new RuntimeException("Unable to get requested file.");
        }
        return fileByDownloadHash;
    }

    private String getFolderNameByFileType(final User requester, final File file, final File.FileType fileType) {
        String locationFolderName = "";
        String nickName = requester.getNickName();
        if (this.fileService.isFileCreator(file.getId(), nickName)) {
            return requester.getEmail();
        } else {
            switch (fileType) {
                case PRIVATE:
                    throw new FtpServerException("You dont have permission to access this file.");
                case SHARED:
                    if (!this.fileService.isUserFromFileSharedUsers(file.getId(), nickName)) {
                        throw new FtpServerException(
                                "This file is not shared with you. You dont have permission to access this file.");
                    }
                    locationFolderName = file.getCreator().getEmail();
                    break;
                default:
                    break;
            }
            return locationFolderName;
        }
    }

    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public void sendResourceByName(final HttpServletResponse response, org.springframework.core.io.Resource resource) {
        try {
            OutputStream responseOutputStream = response.getOutputStream();
            InputStream resourceInputStream = resource.getInputStream();
            String fileName = resource.getFilename();
            final String contentType = this.applicationConfig.getContentTypes().get(fileName.substring(fileName.lastIndexOf(".") + 1));
            setResponseHeaders(response, fileName, contentType);
            final byte[] buffer = createBuffer();
            int bytesRead;
            while ((bytesRead = resourceInputStream.read(buffer, 0, buffer.length)) > 0) {
                responseOutputStream.write(buffer, 0, bytesRead);
            }
            flushAndClose(responseOutputStream);
            resourceInputStream.close();
        } catch (final IOException e) {
            logger.error("errror occured", e);
            throw new FtpServerException("Resource sending failed.");
        }
    }

    @Override
    public String getProfilePicUrl(final String userName, String serverContext) {
        String relativePicturePath;
        org.springframework.core.io.Resource profilePicture = this.storageService.loadProfilePicture(userName);
        if (profilePicture.exists() || profilePicture.isReadable()) {
            relativePicturePath = APIAliases.PROFILE_PIC_ALIAS + userName + ".jpg";
        } else {
            relativePicturePath = "/images/default.jpg";
        }
        return serverContext.concat(relativePicturePath);
    }

    @Override
    public List<DataTransferObject> getFilesISharedWithOtherUsers(Integer firstResult, Integer maxResults) {
        return this.fileService
                .getFilesISharedWithOtherUsers(User.getCurrent().getNickName(), firstResult, maxResults)
                .stream()
                .map((file -> DtoUtil.toSharedFileWithOtherUsersDto(file)))
                .collect(Collectors.toList());
    }

    @Override
    public List<DataTransferObject> getPrivateFiles(Integer firstResult, Integer maxResults) {
        return this.fileService
                .getPrivateFilesForUser(User.getCurrent().getNickName(), firstResult, maxResults)
                .stream()
                .map((DtoUtil::toPrivateFileDto))
                .collect(Collectors.toList());
    }

    @Override
    public List<DataTransferObject> getFilesSharedToMe(Integer firstResult, Integer maxResults) {
        return this.fileService
                .getSharedFilesWithMe(User.getCurrent().getNickName(), firstResult, maxResults)
                .stream()
                .map(file -> DtoUtil.toSharedFileWithMeDto(file))
                .collect(Collectors.toList());
    }

    private byte[] createBuffer() {
        return new byte[ServerConstants.DEFAULT_BUFFER_SIZE];
    }

    private void flushAndClose(OutputStream responseOutputStream) throws IOException {
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    private void setResponseHeaders(HttpServletResponse response, String fileName, String contentType) {
        response.setHeader("Content-Type", contentType == null ? "application/octet-stream" : contentType);
        response.setHeader("Connection", "close");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setHeader("Expires", "0"); // Proxies.
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);
    }
}
