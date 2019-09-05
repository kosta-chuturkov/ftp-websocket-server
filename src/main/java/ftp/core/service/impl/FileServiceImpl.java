package ftp.core.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ftp.core.api.MessagePublishingService;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.dto.DeletedFileDto;
import ftp.core.model.dto.FileSharedWithUsersDto;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.dto.PersonalFileDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.entities.File;
import ftp.core.model.entities.File.FileType;
import ftp.core.model.entities.FileSharedToUser;
import ftp.core.model.entities.User;
import ftp.core.repository.FileRepository;
import ftp.core.repository.FileSharedToUserRepository;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.DtoUtil;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.handler.Handlers;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("fileService")
@Transactional
public class FileServiceImpl implements FileService {

    private UserService userService;
    private FileRepository fileRepository;
    private MessagePublishingService messagePublishingService;
    private FileSharedToUserRepository fileSharedToUserRepository;

    @Autowired
    public FileServiceImpl(UserService userService,
                           FileRepository fileRepository, MessagePublishingService messagePublishingService, FileSharedToUserRepository fileSharedToUserRepository) {
        this.userService = userService;
        this.fileRepository = fileRepository;
        this.messagePublishingService = messagePublishingService;
        this.fileSharedToUserRepository = fileSharedToUserRepository;
    }

    @Override
    public File getFileByDownloadHash(final String downloadHash) {
        return this.fileRepository.findByDownloadHash(downloadHash);
    }

    @Override
    public File findByDeleteHashAndCreatorNickName(final String deleteHash, final String creatorNickName) {
        return this.fileRepository.findByDeleteHashAndCreatorNickName(deleteHash, creatorNickName);
    }

    @Override
    public void saveFile(File fileToBeSaved, Set<String> userNickNames) {
        final User currentUser = User.getCurrent();
        final long remainingStorage = currentUser.getRemainingStorage();
        long fileSize = fileToBeSaved.getFileSize();
        if (remainingStorage < fileSize) {
            throw new FtpServerException(
                    "You are exceeding your upload limit:" + FileUtils
                            .byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT)
                            + ". You have: " + FileUtils.byteCountToDisplaySize(remainingStorage)
                            + " remainig storage.");
        }
        Set<String> validatedUsers = validateUserNickNames(userNickNames);
        final File savedFile = fileRepository.save(fileToBeSaved);
        if (savedFile != null) {
            shareFileWithUsers(savedFile, userNickNames);
            this.userService
                    .updateRemainingStorageForUser(fileSize, currentUser.getEmail(), remainingStorage);
            if (!validatedUsers.isEmpty()) {
                SharedFileDto data = DtoUtil.toSharedFileWithMeDto(savedFile);
                validatedUsers.forEach(user -> this.messagePublishingService.publish(user,
                        new JsonResponse<SharedFileDto>(new PageImpl<>(Lists.newArrayList(data)),
                                Handlers.FILES_SHARED_WITH_ME_HANDLER.getHandlerName())));
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
                .map(NickNameProjection::getNickName)
                .collect(Collectors.toSet());

        List<String> invalidUserNames = users
                .stream()
                .filter(s1 -> !foundNickNames.contains(s1))
                .collect(Collectors.toList());
        if (!invalidUserNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot share files to users [" + invalidUserNames.toString() + "].");
        }
        return foundNickNames;
    }

    public boolean isUserFromFileSharedUsers(final File fileId, final String nickName) {
        return fileSharedToUserRepository.existsByUserAndFile(userService.findUserByNickName(nickName), fileId);
    }

    @Override
    public boolean isFileCreator(final Long fileId, final String userNickName) {
        final Optional<File> exists = fileRepository.findById(fileId);
        if (!exists.isPresent()) {
            return false;
        }
        final User userByNickName = this.userService.findUserByNickName(userNickName);
        return exists.get().getCreator().getNickName().equals(userByNickName.getNickName());
    }

    @Override
    public Page<FileSharedWithUsersDto> getFilesISharedWithOtherUsers(final String userNickName, Pageable pageable) {
        return this.fileRepository.findByFileTypeAndCreatorNickName(FileType.SHARED, userNickName, pageable);
    }

    @Override
    public Page<PersonalFileDto> getPrivateFilesForUser(final String userNickName, Pageable pageable) {
        return this.fileRepository.findByCreatorNickNameAndFileType(userNickName, FileType.PRIVATE, pageable);
    }

    @Override
    public Page<SharedFileDto> getSharedFilesWithCurrent(String userNickName, Pageable pageable) {
        return this.fileSharedToUserRepository.findByUser(userService.findUserByNickName(userNickName), pageable);
    }

    private File updateUsersForFile(final String fileHash, final List<User> updatedUserNickNames) {
        final File file = findByDeleteHashAndCreatorNickName(fileHash, User.getCurrent().getNickName());
        if (file == null) {
            throw new FtpServerException("File not found...");
        }
        final String downloadHash = file.getDownloadHash();
        //TODO delete removed and add new ones
        List<User> toAdd = Lists.newArrayList();
        List<User> toRemove = Lists.newArrayList();
        Map<String, User> newUserToNickname =
                updatedUserNickNames
                        .stream()
                        .collect(Collectors.toMap(User::getNickName, Function.identity()));
        Map<String, User> persistentUserToNickname = fileSharedToUserRepository
                .findByFile(file)
                .stream()
                .map(FileSharedToUser::getUser)
                .collect(Collectors.toMap(User::getNickName, Function.identity()));

        for (String nickName : persistentUserToNickname.keySet()) {
            User user = persistentUserToNickname.get(nickName);
            if (!newUserToNickname.containsKey(nickName)) {
                toRemove.add(user);
            }
        }

        for (String nickName : newUserToNickname.keySet()) {
            User user = newUserToNickname.get(nickName);
            if (!persistentUserToNickname.containsKey(nickName)) {
                toAdd.add(user);
            }
        }

        for (User user : toAdd) {
            shareFileWithUser(file, user);
        }


        // if user in updatedUserNickNames and in persistentSharedToUsers keep
        //if user in updatedUserNickNames but not in persistentSharedToUsers add
        // if user in persistentSharedToUsers but not in updatedUserNickNames delete
        DeletedFileDto deletedFileDto = new DeletedFileDto(downloadHash);
        toRemove.forEach(user -> {
            JsonResponse data = new JsonResponse<>(
                    new PageImpl<>(Lists.newArrayList(deletedFileDto)),
                    Handlers.DELETED_FILE.getHandlerName());
            this.messagePublishingService.publish(user.getNickName(), data);
        });
        return file;
    }

    @Override
    public void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto) {
        List<User> updatedUserList = Lists.newArrayList();
        for (final ModifiedUserDto usersDto : modifiedUserDto) {
            final String name = usersDto.getName();
            final String escapedUserName = StringEscapeUtils.escapeSql(name);
            final User userByNickName = this.userService.findUserByNickName(escapedUserName);
            if (userByNickName == null) {
                throw new FtpServerException("Wrong parameters");
            }
            if (escapedUserName.equals(User.getCurrent().getNickName())) {
                throw new FtpServerException("Cant share file with yourself.");
            }
            updatedUserList.add(userByNickName);
        }

        final File file = updateUsersForFile(deleteHash, updatedUserList);
        final DataTransferObject fileDto = DtoUtil.toSharedFileWithMeDto(file);
        for (final User userNickName : updatedUserList) {
            this.messagePublishingService.publish(userNickName.getNickName(),
                    new JsonResponse<>(new PageImpl<>(Lists.newArrayList(fileDto)),
                            Handlers.FILES_SHARED_WITH_ME_HANDLER.getHandlerName()));
        }
    }

    @Override
    public void delete(Long id) {
        fileRepository.deleteById(id);
    }

    @Override
    public File save(File file) {
        return fileRepository.save(file);
    }

    @Override
    public Optional<File> findById(Long fileId) {
        return this.fileRepository.findById(fileId);
    }

    @Override
    public void shareFileWithUsers(File file, Set<String> sharedWithUsers) {
        sharedWithUsers
                .forEach(nickName -> shareFileWithUser(file, userService.findUserByNickName(nickName)));
    }

    @Override
    public void shareFileWithUser(File file, User user) {
        boolean exists = fileSharedToUserRepository.existsByUserAndFile(user, file);
        if (!exists) {
            fileSharedToUserRepository.save(new FileSharedToUser(file, user));
        }
    }

    @Override
    public Set<String> getListOfUsersFileIsSharedWith(File findByDeleteHash) {
        return fileSharedToUserRepository
                .findByFile(findByDeleteHash)
                .stream()
                .map(fileSharedToUser -> fileSharedToUser.getUser().getNickName())
                .collect(Collectors.toSet());
    }

    @Override
    public List<File> findAllFiles() {
        return fileRepository.findAll();
    }
}
