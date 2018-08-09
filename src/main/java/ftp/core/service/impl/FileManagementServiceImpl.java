package ftp.core.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import ftp.core.config.ApplicationConfig;
import ftp.core.config.FtpConfigurationProperties;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.DeletedFileDto;
import ftp.core.model.dto.DeletedFilesDto;
import ftp.core.model.dto.FileWithSharedUsersWithMeDto;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.PrivateFileWithMeDto;
import ftp.core.model.dto.SharedFileWithMeDto;
import ftp.core.model.dto.UploadedFilesDto;
import ftp.core.model.entities.File;
import ftp.core.model.entities.User;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.face.StorageService;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.DtoUtil;
import ftp.core.util.ServerUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("fileManagementService")
public class FileManagementServiceImpl implements FileManagementService {

  private static final Logger logger = Logger.getLogger(FileManagementServiceImpl.class);

  private UserService userService;
  private FileService fileService;
  private EventService eventService;
  private ResourceLoader resourceLoader;
  private StorageService storageService;
  private ApplicationConfig applicationConfig;

  @Autowired
  public FileManagementServiceImpl(UserService userService,
      FileService fileService, EventService eventService, StorageService storageService,
      ApplicationConfig applicationConfig, ResourceLoader resourceLoader) {
    this.userService = userService;
    this.fileService = fileService;
    this.eventService = eventService;
    this.storageService = storageService;
    this.applicationConfig = applicationConfig;
    this.resourceLoader = resourceLoader;
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
      org.springframework.core.io.Resource profilePicture = this.storageService
          .loadProfilePicture(nickName);
      createImageThumbnail(profilePicture.getFile(), 50, 50);
      profilePicture.getInputStream().close();
      final JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("imageUrl",
          getProfilePicUrl(nickName, this.applicationConfig.getServerAddress()));
      return jsonObject.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public UploadedFilesDto<JsonFileDto> uploadFile(final MultipartFile multipartFile,
      final Set<String> userNickNames) {
    final String fileUploadServerUrl =
        this.applicationConfig.getServerAddress() + APIAliases.DOWNLOAD_FILE_ALIAS;
    User currentUser = User.getCurrent();
    final Long token = currentUser.getToken();
    final String fileName = StringEscapeUtils.escapeSql(multipartFile.getOriginalFilename());
    final long currentTime = System.currentTimeMillis();
    final String tempFileName = Long.toString(currentTime);
    final String serverFileName = tempFileName + "_" + fileName;
    final String deleteHash = ServerUtil
        .hashSHA256(ServerUtil.hashSHA256(serverFileName + token) + ServerConstants.DELETE_SALT);
    final String downloadHash = ServerUtil
        .hashSHA256((serverFileName + token) + ServerConstants.DOWNLOAD_SALT);

    final File fileToBeSaved = new File.Builder()
        .withName(fileName)
        .withTimestamp(new Date(currentTime))
        .withDownloadHash(downloadHash)
        .withDeleteHash(deleteHash)
        .withFileSize(multipartFile.getSize())
        .withCreator(currentUser)
        .withSharedWithUsers(userNickNames)
        .withFileType(userNickNames.isEmpty() ? File.FileType.PRIVATE : File.FileType.SHARED)
        .build();
    this.fileService.saveFile(fileToBeSaved);
    this.storageService
        .store(getInputStream(multipartFile), serverFileName, currentUser.getEmail());
    return buildResponseObject(multipartFile, fileUploadServerUrl, fileName, deleteHash,
        downloadHash);

  }

  private InputStream getInputStream(MultipartFile file) {
    try {
      return file.getInputStream();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private void createImageThumbnail(java.io.File targetFile, int width, int height)
      throws IOException {
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

  private UploadedFilesDto<JsonFileDto> buildResponseObject(MultipartFile file,
      String serverContextAddress, String fileName, String deleteHash, String downloadHash) {
    JsonFileDto dtoWrapper = new JsonFileDto.Builder()
        .withName(StringEscapeUtils.escapeHtml(fileName))
        .withSize(Long.toString(file.getSize()))
        .withUrl((serverContextAddress + downloadHash))
        .withDeleteUrl((serverContextAddress + ServerConstants.DELETE_ALIAS + deleteHash))
        .withDeleteType("GET")
        .build();
    return new UploadedFilesDto<>(Lists.newArrayList(dtoWrapper));
  }

  @Override
  public DeletedFilesDto deleteFiles(final String deleteHash) {
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
    final String storageInfo =
        FileUtils.byteCountToDisplaySize(updatedUser.getRemainingStorage()) + " left from "
            + FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT) + ".";
    try {
      HashMap<String, String> objectObjectHashMap = Maps.newHashMap();
      objectObjectHashMap.put(name, Boolean.TRUE.toString());
      return new DeletedFilesDto(objectObjectHashMap, storageInfo);
    } finally {
      this.storageService
          .deleteResource(getFilenameWithTimestamp(timestamp, name), updatedUser.getEmail());
      this.eventService
          .fireRemovedFileEvent(usersToBeNotifiedFileDeleted, new DeletedFileDto(downloadHash));
    }
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
    org.springframework.core.io.Resource resource = this.storageService
        .loadProfilePicture(userName);
    try {
      if (ServerUtil.existsAndIsReadable(resource)) {
        return new FileSystemResource(resource.getFile());
      } else {
        org.springframework.core.io.Resource defaultPic = this.resourceLoader
            .getResource(ServerConstants.DEFAULT_PROFILE_PICTURE);
        return new FileSystemResource(defaultPic.getFile());
      }
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
      org.springframework.core.io.Resource resource = this.storageService
          .loadAsResource(getFilenameWithTimestamp(timestamp, fileName), fileLocationFolder);
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

  private String getFolderNameByFileType(final User requester, final File file,
      final File.FileType fileType) {
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
    return serverContext.concat(APIAliases.PROFILE_PIC_ALIAS + userName + ".jpg");
  }

  @Override
  public List<FileWithSharedUsersWithMeDto> getFilesISharedWithOtherUsers(Integer firstResult,
      Integer maxResults, String nickName) {
    return this.fileService
        .getFilesISharedWithOtherUsers(nickName, firstResult, maxResults)
        .stream()
        .map((DtoUtil::toSharedFileWithOtherUsersDto))
        .collect(Collectors.toList());
  }

  @Override
  public List<PrivateFileWithMeDto> getPrivateFiles(Integer firstResult, Integer maxResults,
      String nickName) {
    return this.fileService
        .getPrivateFilesForUser(nickName, firstResult, maxResults)
        .stream()
        .map((DtoUtil::toPrivateFileDto))
        .collect(Collectors.toList());
  }

  @Override
  public List<SharedFileWithMeDto> getFilesSharedToMe(Integer firstResult, Integer maxResults,
      String nickName) {
    return this.fileService
        .getSharedFilesWithMe(nickName, firstResult, maxResults)
        .stream()
        .map(DtoUtil::toSharedFileWithMeDto)
        .collect(Collectors.toList());
  }

}
