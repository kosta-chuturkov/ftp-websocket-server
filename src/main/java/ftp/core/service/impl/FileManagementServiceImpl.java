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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service("fileManagementService")
public class FileManagementServiceImpl implements FileManagementService {

    private static final Logger logger = Logger.getLogger(FileManagementServiceImpl.class);

    @Resource
    private Gson gson;
    @Resource
    private Executor executor;
    @Resource
    private JsonParser jsonParser;
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private EventService eventService;
    @Resource
    private StorageService storageService;
    @Autowired
    private ApplicationConfig applicationConfig;


    private FtpConfigurationProperties ftpConfigurationProperties;

    @Autowired
    public FileManagementServiceImpl(FtpConfigurationProperties ftpConfigurationProperties) {

        this.ftpConfigurationProperties = ftpConfigurationProperties;
    }

    @Override
    public String updateProfilePicture(final MultipartFile file) {

        try {
            final String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
            final String extension = FilenameUtils.getExtension(fileName);
            checkFileExtention(extension);
            String nickName = User.getCurrent().getNickName();
            final String serverFileName = nickName + "." + extension;
            this.storageService.storeProfilePicture(getInputStream(file), serverFileName);
            org.springframework.core.io.Resource profilePicture = this.storageService.loadProfilePicture(nickName);
            createImageThumbnail(profilePicture.getFile(), 50, 50);
            profilePicture.getInputStream().close();
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("imageUrl", getProfilePicUrl(nickName, this.applicationConfig.getServerAddress()));
            return jsonObject.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadFile(final MultipartFile file,
                             final String userNickNames) {
        final String fileUploadServerUrl = this.applicationConfig.getServerAddress() + APIAliases.DOWNLOAD_FILE_ALIAS;
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
        this.storageService.store(getInputStream(file), serverFileName, User.getCurrent().getEmail());
        return buildResponseObject(file, fileUploadServerUrl, fileName, deleteHash, downloadHash).toString();

    }

    private InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void createImageThumbnail(java.io.File targetFile, int width, int height) throws IOException {
        Thumbnails.of(targetFile)
                .size(width, height)
                .outputFormat("jpg")
                .toFiles(Rename.NO_CHANGE);
    }

    final Set<String> ALLOWED_EXTENTIONS = Sets.newHashSet("jpg", "JPG");

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

        return geAstJsonObject(new ResponseModelAdapter.Builder().withBaseFileDto(dtoWrapper).build());
    }

    public JSONObject geAstJsonObject(final ResponseModelAdapter dtoWrapper) {
        final JSONObject parent = new JSONObject();
        final JSONArray json = new JSONArray();
        final JSONObject jsonObject = new JSONObject(this.gson.toJson(dtoWrapper));
        json.put(jsonObject.get("baseFileDto"));
        parent.put("files", json);
        return parent;
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
    public String deleteFiles(final String deleteHash) {
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
        try {
            return buildResponse(name, storageInfo).toString();
        } finally {
            this.executor.execute(() -> {
                this.storageService.deleteResource(getFilenameWithTimestamp(timestamp, name), updatedUser.getEmail());
                this.eventService.fireRemovedFileEvent(usersToBeNotifiedFileDeleted, new DeletedFileDto(downloadHash));
            });
        }
    }

    private JSONObject buildResponse(String fileName, String storedBytes) {
        final JSONObject parent = new JSONObject();
        final JSONArray json = new JSONArray();
        final JSONObject jsono = new JSONObject();
        jsono.put(fileName, "true");
        json.put(jsono);
        parent.put("files", json);
        parent.put("storedBytes", storedBytes);
        return parent;
    }

    private File getFile(String deleteHash, String nickName) {
        final File findByDeleteHash = this.fileService.findByDeleteHash(deleteHash, nickName);
        if (findByDeleteHash == null) {
            throw new RuntimeException("File does not exist.");
        }
        return findByDeleteHash;
    }

    @Override
    public FileSystemResource sendProfilePicture(String userName) {
        org.springframework.core.io.Resource resource = this.storageService.loadProfilePicture(userName);
        try {
            return new FileSystemResource(resource.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileSystemResource downloadFile(String downloadHash) {
        final User current = User.getCurrent();
        final File fileByDownloadHash = getFile(downloadHash);
        final Date timestamp = fileByDownloadHash.getTimestamp();
        final String fileName = fileByDownloadHash.getName();
        final File.FileType fileType = fileByDownloadHash.getFileType();
        String fileLocationFolder = getFolderNameByFileType(current, fileByDownloadHash, fileType);
        try {
            org.springframework.core.io.Resource resource = this.storageService.loadAsResource(getFilenameWithTimestamp(timestamp, fileName), fileLocationFolder);
            return new FileSystemResource(resource.getFile());
        } catch (IOException e) {
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

    @Override
    public String getProfilePicUrl(final String userName, String serverContext) {
        String relativePicturePath;
        org.springframework.core.io.Resource profilePicture = this.storageService.loadProfilePicture(userName);
        if (profilePicture.exists() || profilePicture.isReadable()) {
            relativePicturePath = APIAliases.PROFILE_PIC_ALIAS + userName + ".jpg";
        } else {
            relativePicturePath = "/static/images/default.jpg";
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
}
