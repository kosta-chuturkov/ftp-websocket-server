package ftp.core.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ftp.core.common.model.AbstractEntity;
import ftp.core.common.model.File;
import ftp.core.common.model.File.FileType;
import ftp.core.common.model.User;
import ftp.core.common.model.dto.DeletedFileDto;
import ftp.core.common.model.dto.FileDto;
import ftp.core.common.model.dto.SharedFileDto;
import ftp.core.common.util.ServerConstants;
import ftp.core.persistance.face.dao.FileDao;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.generic.AbstractGenericService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service("fileService")
public class FileServiceImpl extends AbstractGenericService<File, Long> implements FileService {

    @Resource
    private FileDao fileDao;

    @Resource
    private UserService userService;

    @Resource
    private EventService eventService;

    @Override
    public File getFileByDownloadHash(final String downloadHash) {
        return this.fileDao.getFileByDownloadHash(downloadHash);
    }

    @Override
    public void deleteFile(final String deleteHash, final String creatorNickName) {
        this.fileDao.deleteFile(deleteHash, creatorNickName);
    }

    @Override
    public File findByDeleteHash(final String deleteHash, final String creatorNickName) {
        return this.fileDao.findByDeleteHash(deleteHash, creatorNickName);
    }

    @Override
    public void addUserToFile(final Long fileId, final String userToAdd) {
        final AbstractEntity findOne = findOne(fileId);
        if (findOne != null) {
            final File file = (File) findOne;
            file.addUser(userToAdd);
            update(file);
        }
    }


    public void createFileRecord(final String fileNameEscaped, final long timestamp, final int modifier, final String userToSendFilesTo,
                                 final long fileSize, final String deleteHash, final String downloadHash) {
        final User currentUser = User.getCurrent();
        final long remainingStorage = currentUser.getRemainingStorage();
        if (remainingStorage < fileSize) {
            throw new FtpServerException(
                    "You are exceeding your upload limit:" + FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT)
                            + ". You have: " + FileUtils.byteCountToDisplaySize(remainingStorage) + " remainig storage.");
        }
        final ftp.core.common.model.File file = new ftp.core.common.model.File();
        file.setName(fileNameEscaped);
        file.setTimestamp(new Date(timestamp));
        file.setDownloadHash(downloadHash);
        file.setDeleteHash(deleteHash);
        file.setFileSize(fileSize);
        file.setCreator(currentUser);
        file.setFileType(ftp.core.common.model.File.FileType.getById(modifier));
        final Long savedFileId = save(file);
        if (savedFileId != null) {
            this.userService.updateRemainingStorageForUser(fileSize, currentUser.getId(), remainingStorage);
            if (modifier == FileType.SHARED.getType()) {
                final User userToShareTheFileWith = this.userService.checkAndGetUserToSendFilesTo(userToSendFilesTo);
                addUserToFile(savedFileId, userToShareTheFileWith.getNickName());
                this.userService.addFileToUser(savedFileId, currentUser.getId());
                final FileDto fileDto = new SharedFileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                        file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
                this.eventService.fireSharedFileEvent(userToSendFilesTo, fileDto);
            }
        } else {
            throw new RuntimeException("Unable to add file!");
        }
    }

    public boolean isUserFromFileSharedUsers(final Long fileId, final String nickName) {
        final AbstractEntity exists = findOne(fileId);
        if (exists == null) {
            return false;
        }
        final Set<String> sharedWithUsers = ((File) exists).getSharedWithUsers();
        return sharedWithUsers.contains(nickName);
    }

    @Override
    public boolean isFileCreator(final Long fileId, final String userNickName) {
        final AbstractEntity exists = findOne(fileId);
        if (exists == null) {
            return false;
        }
        final File file = ((File) exists);
        final User userByNickName = this.userService.getUserByNickName(userNickName);
        return file.getCreator().getNickName().equals(userByNickName.getNickName());
    }

    @Override
    public List<File> getSharedFilesForUser(final String userNickName, final int firstResult, final int maxResults) {
        return this.fileDao.getSharedFilesForUser(userNickName, firstResult, maxResults);
    }

    @Override
    public List<File> getPrivateFilesForUser(final String userNickName, final int firstResult, final int maxResults) {
        return this.fileDao.getPrivateFilesForUser(userNickName, firstResult, maxResults);
    }

    @Override
    public List<Long> getSharedFilesWithUsersIds(final Long userId, final int firstResult, final int maxResults) {
        return this.fileDao.getSharedFilesWithUsers(userId, firstResult, maxResults);
    }

    @Override
    public File findWithSharedUsers(final Long fileId) {
        return this.fileDao.findWithSharedUsers(fileId);
    }

    @Override
    public File updateUsersForFile(final String fileHash, final Set<String> userNickNames) {
        final File file = findByDeleteHash(fileHash, User.getCurrent().getNickName());
        if (file == null) {
            throw new FtpServerException("File not found...");
        }
        final String downloadHash = file.getDownloadHash();
        final Set<String> persistentSharedToUsers = Sets.newHashSet(file.getSharedWithUsers());
        persistentSharedToUsers.removeAll(userNickNames);
        file.setSharedWithUsers(userNickNames);
        update(file);
        this.eventService.fireRemovedFileEvent(Lists.newArrayList(persistentSharedToUsers), new DeletedFileDto(downloadHash));
        return file;
    }

}
