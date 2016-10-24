package ftp.core.service.impl;

import com.google.common.collect.Sets;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.dto.DeletedFileDto;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.entities.AbstractEntity;
import ftp.core.model.entities.File;
import ftp.core.model.entities.File.FileType;
import ftp.core.model.entities.User;
import ftp.core.repository.FileRepository;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.generic.AbstractGenericService;
import ftp.core.util.DtoUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("fileService")
@Transactional
public class FileServiceImpl extends AbstractGenericService<File, Long> implements FileService {

    @Resource
    private FileRepository fileRepository;

    @Resource
    private UserService userService;

    @Resource
    private EventService eventService;

    @Override
    public File getFileByDownloadHash(final String downloadHash) {
        return this.fileRepository.findByDownloadHash(downloadHash);
    }

    @Override
    public File findByDeleteHash(final String deleteHash, final String creatorNickName) {
        return this.fileRepository.findByDeleteHashAndCreatorNickName(deleteHash, creatorNickName);
    }

    public void createFileRecord(final String fileNameEscaped, final long timestamp, final Set<String> users,
                                 final long fileSize, final String deleteHash, final String downloadHash) {
        final User currentUser = User.getCurrent();
        final long remainingStorage = currentUser.getRemainingStorage();
        if (remainingStorage < fileSize) {
            throw new FtpServerException(
                    "You are exceeding your upload limit:" + FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT)
                            + ". You have: " + FileUtils.byteCountToDisplaySize(remainingStorage) + " remainig storage.");
        }
        Set<String> validatedUsers = validateUserNickNames(users);
        final File file = new File.Builder()
                .withName(fileNameEscaped)
                .withTimestamp(new Date(timestamp))
                .withDownloadHash(downloadHash)
                .withDeleteHash(deleteHash)
                .withFileSize(fileSize)
                .withCreator(currentUser)
                .withSharedWithUsers(validatedUsers)
                .withFileType(users.isEmpty() ? FileType.PRIVATE : FileType.SHARED)
                .build();
        final File savedFile = saveAndFlush(file);
        if (savedFile != null) {
            this.userService.updateRemainingStorageForUser(fileSize, currentUser.getId(), remainingStorage);
            this.userService.addFileToUser(savedFile.getId(), currentUser.getId());
            if(!validatedUsers.isEmpty()) {
                this.eventService.fireSharedFileEvent(validatedUsers, DtoUtil.toSharedFileWithMeDto(savedFile));
            }
        } else {
            throw new RuntimeException("Unable to add file!");
        }
    }

    private Set<String> validateUserNickNames(Set<String> users) {
        if (users.contains(User.getCurrent().getNickName())) {
            throw new IllegalArgumentException("Cannot share files with yourself");
        }
        Set<String> foundNickNames = this.userService.findByNickNameIn(users)
                .stream()
                .map(nickNameProjection -> nickNameProjection.getNickName())
                .collect(Collectors.toSet());

        List<String> invalidUserNames = users
                .stream()
                .filter(s1 -> !foundNickNames.contains(s1))
                .collect(Collectors.toList());
        if (!invalidUserNames.isEmpty()) {
            throw new IllegalArgumentException("Cannot share files to users [" + invalidUserNames.toString() + "].");
        }
        return foundNickNames;
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
    public List<File> getFilesISharedWithOtherUsers(final String userNickName, final int firstResult, final int maxResults) {
        return this.fileRepository.findByCreatorNickNameAndFileType(userNickName, FileType.SHARED, new PageRequest(firstResult, maxResults));
    }

    @Override
    public List<File> getPrivateFilesForUser(final String userNickName, final int firstResult, final int maxResults) {
        return this.fileRepository.findByCreatorNickNameAndFileType(userNickName, FileType.PRIVATE, new PageRequest(firstResult, maxResults));
    }

    @Override
    public List<File> getSharedFilesWithMe(String userNickName, int firstResult, int maxResults) {
        return this.fileRepository.findSharedFilesWithMe(userNickName, FileType.SHARED, new PageRequest(firstResult, maxResults));
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
        saveAndFlush(file);
        this.eventService.fireRemovedFileEvent(persistentSharedToUsers, new DeletedFileDto(downloadHash));
        return file;
    }

    @Override
    public void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto) {
        final Set<String> userNickNames = Sets.newHashSet();
        if (modifiedUserDto.size() == 1) {
            final String userNickName = modifiedUserDto.iterator().next().getName();
            if (userNickName == null || "-1".equals(userNickName)) {
                updateUsersForFile(deleteHash, userNickNames);
                return;
            }
        }

        for (final ModifiedUserDto usersDto : modifiedUserDto) {
            final String name = usersDto.getName();
            final String escapedUserName = StringEscapeUtils.escapeSql(name);
            final User userByNickName = this.userService.getUserByNickName(escapedUserName);
            if (userByNickName == null) {
                throw new FtpServerException("Wrong parameters");
            }
            if (escapedUserName.equals(User.getCurrent().getNickName())) {
                throw new FtpServerException("Cant share file with yourself.");
            }
            userNickNames.add(escapedUserName);
        }

        final File file = updateUsersForFile(deleteHash, userNickNames);
        final DataTransferObject fileDto = DtoUtil.toSharedFileWithMeDto(file);
        for (final String userNickName : userNickNames) {
            this.eventService.fireSharedFileEvent(userNickName, fileDto);
        }
    }

    @Override
    public List<File> findByCreatorId(Long creatorId, Pageable pageable) {
        return this.fileRepository.findByCreatorId(creatorId, pageable);
    }

}
