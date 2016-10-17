package ftp.core.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import ftp.core.config.ServerConfigurator;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.DeletedFileDto;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.ResponseModelAdapter;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.ServerUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ftp.core.util.ServerUtil.getProtocol;

@Service("fileManagementService")
public class FileManagementServiceImpl implements FileManagementService {

    final Set<String> ALLOWED_EXTENTIONS = Sets.newHashSet("jpg");
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

    public String updateProfilePicture(final HttpServletRequest request,
                                       final MultipartFile file) throws IOException {

        final String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
        final String extension = FilenameUtils.getExtension(fileName);
        checkFileExtention(extension);
        final String serverFileName = User.getCurrent().getNickName() + "." + extension;
        final String imageUrlAddress = buildImageUrlAddress(request, serverFileName);

        final java.io.File targetFile = createFileInFolder(serverFileName, ServerConfigurator.getProfilePicsFolder());
        transferToTargetFile(file, targetFile);

        createImageThumbnail(targetFile, 50, 50);
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imageUrl", imageUrlAddress);
        return jsonObject.toString();


    }

    public String uploadFile(final HttpServletRequest request,
                             final MultipartFile file, String modifier,
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
        this.fileService.createFileRecord(fileName, currentTime, getModifier(modifier), users, file.getSize(),
                deleteHash, downloadHash);
        final java.io.File userFolder = getUserFolder(User.getCurrent().getEmail());
        final java.io.File targetFile = createFileInFolder(serverFileName, userFolder);
        transferToTargetFile(file, targetFile);
        return buildResponseObject(file, serverContextAddress, fileName, deleteHash, downloadHash).toString();

    }

    private void transferToTargetFile(MultipartFile file, java.io.File targetFile) throws IOException {
        file.transferTo(targetFile);
    }

    private String buildImageUrlAddress(HttpServletRequest request, String serverFileName) {
        final int port = request.getServerPort();
        final String host = request.getServerName();
        return ServerUtil.getProtocol(request) + host + ":" + port
                + APIAliases.PROFILE_PIC_ALIAS + serverFileName;
    }

    private void createImageThumbnail(java.io.File targetFile, int width, int height) throws IOException {
        Thumbnails.of(targetFile)
                .size(width, height)
                .outputFormat("jpg")
                .toFiles(Rename.NO_CHANGE);
    }

    private void checkFileExtention(String extension) {
        if (!ALLOWED_EXTENTIONS.contains(extension)) {
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

    private java.io.File createFileInFolder(String serverFileName, java.io.File userFolder) throws IOException {
        final java.io.File targetFile = new java.io.File(userFolder, serverFileName);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        targetFile.createNewFile();
        return targetFile;
    }

    private Set<String> getFileSharedUsersAsSet(final String userNickNames) {
        final Set<String> users = Sets.newHashSet();
        if (userNickNames.isEmpty()) return users;
        final JsonElement elem = this.jsonParser.parse(userNickNames);
        final JsonArray asJsonArray = elem.getAsJsonArray();
        for (final JsonElement jsonElement : asJsonArray) {
            final String name = jsonElement.getAsJsonObject().get("name").getAsString();
            users.add(name);
        }
        return users;
    }

    private int getModifier(final String modifierString) throws IOException {
        int modifier = -1;
        try {
            modifier = Integer.parseInt(modifierString);
            if (checkModifier(modifier)) {
                throw new FtpServerException(
                        "Modifier parameter is incorrect:" + modifierString + ".");
            }
        } catch (final NumberFormatException e) {
            throw new FtpServerException(
                    "Modifier parameter is incorrect:" + modifierString + ":" + ".The supported type is int.");
        }
        return modifier;
    }

    private boolean checkModifier(final int modifier) {
        return ftp.core.model.entities.File.FileType.getById(modifier) == null;
    }

    private java.io.File getUserFolder(final String userName) {
        final java.io.File clientDir = new java.io.File(ServerConfigurator.getServerStorageFile(), userName);
        createUserDirIfNotExist(clientDir);
        return clientDir;
    }

    private void createUserDirIfNotExist(final java.io.File clientDir) {
        if (!clientDir.exists()) {
            clientDir.mkdir();
        }
    }


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
        final String deletePath = ServerConstants.SERVER_STORAGE_FOLDER_NAME.concat("/").concat(updatedUser.getEmail())
                .concat("/").concat(timestamp.getTime() + "_" + name);
        final java.io.File fileToDelete = new java.io.File(deletePath);
        if (fileToDelete != null && fileToDelete.exists()) {
            FileDeleteStrategy.FORCE.deleteQuietly(fileToDelete);
        }
        this.eventService.fireRemovedFileEvent(usersToBeNotifiedFileDeleted, new DeletedFileDto(downloadHash));
    }

    private File getFile(String deleteHash, String nickName) {
        final File findByDeleteHash = this.fileService.findByDeleteHash(deleteHash, nickName);
        if (findByDeleteHash == null) {
            throw new RuntimeException("File does not exist.");
        }
        return findByDeleteHash;
    }

    public void downloadFile(final HttpServletRequest request, final HttpServletResponse response) {
        sendFile(request, response);
    }

    public void getProfilePic(final HttpServletResponse response, String filename) {
        filename += ".jpg";
        final java.io.File file = new java.io.File(ServerConfigurator.getProfilePicsFolder(), filename);
        ServerUtil.sendResourceByName(response, file.getAbsolutePath(), filename);
    }


    private void sendFile(final HttpServletRequest request, final HttpServletResponse response) {
        final User current = User.getCurrent();
        final String path = request.getServletPath();
        String downloadHash = "";
        if (path != null) {
            downloadHash = path.substring(APIAliases.DOWNLOAD_FILE_ALIAS.length(), path.length());
        }
        final String requesterEmail = current.getEmail();
        final String requesterNickName = current.getNickName();
        final File fileByDownloadHash = getFile(downloadHash);
        final Date timestamp = fileByDownloadHash.getTimestamp();
        final String fileName = fileByDownloadHash.getName();
        final File.FileType fileType = fileByDownloadHash.getFileType();
        String locationFolderName = "";
        if (this.fileService.isFileCreator(fileByDownloadHash.getId(), requesterNickName)) {
            locationFolderName = requesterEmail;
        } else {
            locationFolderName = getFolderNameByFileType(requesterNickName, fileByDownloadHash, fileType);
        }
        final String downloadPath = buildDownloadPath(timestamp, fileName, locationFolderName);
        ServerUtil.sendResourceByName(response, downloadPath, fileByDownloadHash.getName());
    }

    private File getFile(String downloadHash) {
        final File fileByDownloadHash = this.fileService.getFileByDownloadHash(downloadHash);
        if (fileByDownloadHash == null) {
            throw new RuntimeException("Unable to get requested file.");
        }
        return fileByDownloadHash;
    }

    private String buildDownloadPath(Date timestamp, String fileName, String locationFolderName) {
        return ServerConstants.SERVER_STORAGE_FOLDER_NAME.concat("/").concat(locationFolderName)
                .concat("/").concat(timestamp.getTime() + "_" + fileName);
    }

    private String getFolderNameByFileType(final String nickName, final File fileByDownloadHash, final File.FileType fileType) {
        String locationFolderName = "";
        switch (fileType) {
            case PRIVATE:
                throw new FtpServerException("You dont have permission to access this file.");
            case PUBLIC:
                locationFolderName = fileByDownloadHash.getCreator().getEmail();
                break;
            case SHARED:
                if (!this.fileService.isUserFromFileSharedUsers(fileByDownloadHash.getId(), nickName)) {
                    throw new FtpServerException(
                            "This file is not shared with you. You dont have permission to access this file.");
                }
                locationFolderName = fileByDownloadHash.getCreator().getEmail();
                break;
            default:
                break;
        }
        return locationFolderName;
    }
}
