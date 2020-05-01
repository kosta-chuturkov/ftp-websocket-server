package ftp.core.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import ftp.core.api.MessagePublishingService;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.DeletedFileDto;
import ftp.core.model.dto.DeletedFilesDto;
import ftp.core.model.dto.FileSharedWithUsersDto;
import ftp.core.model.dto.FileUpdateRequest;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.PersonalFileDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.dto.UploadedFilesDto;
import ftp.core.model.entities.File;
import ftp.core.model.entities.FileSharedToUser;
import ftp.core.model.entities.User;
import ftp.core.repository.FileSharedToUserRepository;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.face.StorageService;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.ServerUtil;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.handler.Handlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.bus.Event;

import javax.annotation.PostConstruct;

@Service("fileManagementService")
public class FileManagementServiceImpl implements FileManagementService {

    private final UserService userService;
    private final FileService fileService;
    private final MessagePublishingService messagePublishingService;
    private final StorageService storageService;
    private final Gson gson;

    @Autowired
    public FileManagementServiceImpl(@Lazy UserService userService,
                                     FileService fileService,
                                     MessagePublishingService messagePublishingService, StorageService storageService,
                                     FileSharedToUserRepository fileSharedToUserRepository, Gson gson) {
        this.userService = userService;
        this.fileService = fileService;
        this.messagePublishingService = messagePublishingService;
        this.storageService = storageService;
        this.gson = gson;
    }

    @Override
    public UploadedFilesDto<JsonFileDto> uploadFile(final MultipartFile multipartFile,
                                                    final String userNickNamesRaw) {
        HashSet<String> userNickNames = this.gson.fromJson(userNickNamesRaw, HashSet.class);
        User currentUser = User.getCurrent();
        if (currentUser == null) {
            throw new RuntimeException("You are not logged in.");
        }
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
                .withCreator(userService.getUserByEmail(currentUser.getEmail()))
                .withFileType(userNickNames.isEmpty() ? File.FileType.PRIVATE : File.FileType.SHARED)
                .build();
        this.fileService.saveFile(fileToBeSaved, userNickNames);
        this.storageService
                .store(getInputStream(multipartFile), serverFileName, currentUser.getEmail());
        return buildResponseObject(multipartFile, "", fileName, deleteHash,
                downloadHash);

    }

    private InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        User currentU = User.getCurrent();
        if (currentU == null) {
            throw new RuntimeException("You are not logged in.");
        }
        final User current = userService.getUserByEmail(currentU.getEmail());
        final String nickName = current.getNickName();
        final File findByDeleteHash = getFile(deleteHash, nickName);
        final String downloadHash = findByDeleteHash.getDownloadHash();
        final Set<String> sharedWithUsers = fileService.getListOfUsersFileIsSharedWith(findByDeleteHash);
        final List<String> usersToBeNotifiedFileDeleted = Lists.newArrayList(sharedWithUsers);
        usersToBeNotifiedFileDeleted.add(current.getNickName());
        final long fileSize = findByDeleteHash.getFileSize();
        final String name = findByDeleteHash.getName();
        final Date timestamp = findByDeleteHash.getCreatedDate();

        current.setRemainingStorage(current.getRemainingStorage() + fileSize);
        this.userService.save(current);
        this.fileService.delete(findByDeleteHash.getId());

        final User updatedUser = this.userService.getUserByEmail(current.getEmail());
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
            DeletedFileDto deletedFileDto = new DeletedFileDto(downloadHash);
            usersToBeNotifiedFileDeleted.forEach(user -> {
                Event<JsonResponse> data = Event
                        .wrap(new JsonResponse<>(new PageImpl<>(Lists.newArrayList(deletedFileDto)),
                                Handlers.DELETED_FILE.getHandlerName()));
                this.messagePublishingService.publish(user, data);
            });
        }
    }

    private File getFile(String deleteHash, String nickName) {
        final File findByDeleteHash = this.fileService.findByDeleteHashAndCreatorNickName(deleteHash, nickName);
        if (findByDeleteHash == null) {
            throw new RuntimeException("File does not exist.");
        }
        return findByDeleteHash;
    }

    @Override
    public FileSystemResource downloadFile(String downloadHash) {
        final User current = User.getCurrent();
        if (current == null) {
            throw new RuntimeException("You are not logged in.");
        }
        final File fileByDownloadHash = getFile(downloadHash);
        final Date timestamp = fileByDownloadHash.getCreatedDate();
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
                    if (!this.fileService.isUserFromFileSharedUsers(file, nickName)) {
                        throw new FtpServerException(
                                "This file is not shared with you. You dont have permission to access this file.");
                    }
                    locationFolderName = file.getCreatedBy().getEmail();
                    break;
                default:
                    break;
            }
            return locationFolderName;
        }
    }

    @Override
    public Page<FileSharedWithUsersDto> getFilesISharedWithOtherUsers(Pageable pageable) {
        return this.fileService.getFilesISharedWithOtherUsers(
                User.getCurrent() == null ? null : User.getCurrent().getNickName(), pageable);
    }

    @Override
    public Page<PersonalFileDto> getPrivateFiles(Pageable pageable) {
        return this.fileService
                .getPrivateFilesForUser(User.getCurrent() == null ? null : User.getCurrent().getNickName(),
                        pageable);
    }

    @Override
    public Page<SharedFileDto> getFilesSharedToMe(Pageable pageable) {
        return this.fileService
                .getSharedFilesWithCurrent(User.getCurrent() == null ? null : User.getCurrent().getNickName(),
                        pageable);
    }

    @Override
    public File updateFile(FileUpdateRequest updateRequest) {
        final User current = User.getCurrent();
        if (current == null) {
            throw new RuntimeException("You are not logged in.");
        }
        final File file = getFile(updateRequest.getDownloadHash());
        if (file.getCreatedBy() != current) {
            throw new RuntimeException("You are not the owner of this file and cannot update it.");
        }

        fileService.shareFileWithUsers(file, updateRequest.getSharedWithUsers());
        file.setFileType(updateRequest.getFileType());
        file.setName(updateRequest.getName());
        return fileService.save(file);
    }


    @PostConstruct
    public void init() {
        User user = new User();
        user.setEmail(UUID.randomUUID().toString().substring(5));
        user.setNickName("lexter");
        user.setPassword(UUID.randomUUID().toString().substring(5));
        userService.save(user);
    }

    @Override
    public FileSharedToUser test() {
        List<String> names = new ArrayList<>();
        names.add("Alison Marriott");
        names.add("Daniela Roche");
        names.add("Ashton Hartman");
        names.add("Tyrese Hutchinson");
        names.add("Saim Schwartz");
        names.add("Mikaela Duffy");
        names.add("Omari Irwin");
        names.add("Borys Denton");
        names.add("Priya English");
        names.add("Denis Foley");
        boolean flag = false;
        for (String name : names) {
            File file = new File();
            file.setName(name);
            file.setFileSize((long) (Math.random() * 1000));
            file.setDeleteHash(UUID.randomUUID().toString().substring(5));
            file.setDownloadHash(UUID.randomUUID().toString().substring(5));
            if (!flag) {
                file.setFileType(File.FileType.SHARED);
                flag = true;
            } else {
                file.setFileType(File.FileType.PRIVATE);
                flag = false;
            }
            fileService.save(file);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Page<File> getAllFiles(Pageable pageable, String fileType) {
        return fileService.findAllFiles(pageable, fileType);
    }

    @Override
    public Page<File> findByQuery(String query, String fileType, Pageable pageable) {
        return fileService.findByQuery(query, fileType, pageable);
    }

}
