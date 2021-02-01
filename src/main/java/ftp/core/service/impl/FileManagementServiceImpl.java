package ftp.core.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import ftp.core.api.MessagePublishingService;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.*;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.bus.Event;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.*;

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
        final String serverFileName = currentTime + "_" + fileName;
        final String deleteHash = ServerUtil
                .hashSHA256(ServerUtil.hashSHA256(serverFileName + token) + ServerConstants.DELETE_SALT);
        final String downloadHash = ServerUtil
                .hashSHA256((serverFileName + token) + ServerConstants.DOWNLOAD_SALT);

        final File fileToBeSaved = new File.Builder()
                .withName(fileName)
                .withDownloadHash(downloadHash)
                .withDeleteHash(deleteHash)
                .withFileSize(multipartFile.getSize())
                .withCreator(userService.getUserByEmail(currentUser.getEmail()))
                .withFileType(userNickNames.isEmpty() ? File.FileType.PRIVATE : File.FileType.SHARED)
                .build();
        File file = this.fileService.saveFile(fileToBeSaved, userNickNames);
        this.storageService
                .store(getInputStream(multipartFile), file.getCreatedDate().getTime() + "_" + fileName, currentUser.getEmail());
        return buildResponseObject(multipartFile, "", fileName, deleteHash,
                downloadHash);

    }

    @Override
    public void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
        target.flush();
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
    public void downloadFile(String downloadHash, HttpServletResponse response) {
        final User current = User.getCurrent();
        if (current == null) {
            throw new RuntimeException("You are not logged in.");
        }
        final File fileByDownloadHash = getFile(downloadHash);
        final Date timestamp = fileByDownloadHash.getCreatedDate();
        final String fileName = fileByDownloadHash.getName();
        String fileLocationFolder = getFolderNameByFileType(current, fileByDownloadHash);
        try {
            Path resource = this.storageService.load(getFilenameWithTimestamp(timestamp, fileName), fileLocationFolder);
            java.io.File file = resource.toFile();
            String mimeType = deriveMimeType(file);
            response.setContentType(mimeType);
            FileChannel fc = new FileInputStream(file).getChannel();
            fc.transferTo(0, file.length(), Channels.newChannel(response.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String deriveMimeType(java.io.File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());
        return mimeType;
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

    private String getFolderNameByFileType(final User requester, final File file) {
        String locationFolderName = "";
        String nickName = requester.getNickName();
        if (this.fileService.isFileCreator(file.getId(), nickName)) {
            return requester.getEmail();
        } else {
            switch (file.getFileType()) {
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

    @Override
    public FileSharedToUser uploadMockupData() {
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
            file.setCreatedBy(User.getCurrent());
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
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new FileSharedToUser();
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
