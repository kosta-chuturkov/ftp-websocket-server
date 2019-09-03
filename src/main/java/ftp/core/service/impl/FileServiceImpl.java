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
import ftp.core.model.entities.User;
import ftp.core.repository.FileRepository;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.DtoUtil;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.handler.Handlers;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  @Autowired
  public FileServiceImpl(UserService userService,
      FileRepository fileRepository, MessagePublishingService messagePublishingService) {
    this.userService = userService;
    this.fileRepository = fileRepository;
    this.messagePublishingService = messagePublishingService;
  }

  @Override
  public File getFileByDownloadHash(final String downloadHash) {
    return this.fileRepository.findByDownloadHash(downloadHash);
  }

  @Override
  public File findByDeleteHash(final String deleteHash, final String creatorNickName) {
    return this.fileRepository.findByDeleteHashAndCreatorNickName(deleteHash, creatorNickName);
  }

  @Override
  public void saveFile(File fileToBeSaved) {
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
    Set<String> validatedUsers = validateUserNickNames(fileToBeSaved.getSharedWithUsers());
    final File savedFile = fileRepository.save(fileToBeSaved);
    if (savedFile != null) {
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

  public boolean isUserFromFileSharedUsers(final Long fileId, final String nickName) {
    final Optional<File> exists = fileRepository.findById(fileId);
    if (!exists.isPresent()) {
      return false;
    }
    final Set<String> sharedWithUsers = exists.get().getSharedWithUsers();
    return sharedWithUsers.contains(nickName);
  }

  @Override
  public boolean isFileCreator(final Long fileId, final String userNickName) {
    final Optional<File> exists = fileRepository.findById(fileId);
    if (!exists.isPresent()) {
      return false;
    }
    final User userByNickName = this.userService.getUserByNickName(userNickName);
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
  public Page<SharedFileDto> getSharedFilesWithMe(String userNickName, Pageable pageable) {
    return this.fileRepository.findSharedFilesWithMe(userNickName, FileType.SHARED, pageable);
  }

  private File updateUsersForFile(final String fileHash, final Set<String> userNickNames) {
    final File file = findByDeleteHash(fileHash, User.getCurrent().getNickName());
    if (file == null) {
      throw new FtpServerException("File not found...");
    }
    final String downloadHash = file.getDownloadHash();
    final Set<String> persistentSharedToUsers = Sets.newHashSet(file.getSharedWithUsers());
    persistentSharedToUsers.removeAll(userNickNames);
    file.setSharedWithUsers(userNickNames);
    fileRepository.save(file);
    DeletedFileDto deletedFileDto = new DeletedFileDto(downloadHash);
    persistentSharedToUsers.forEach(user -> {
      JsonResponse data = new JsonResponse<>(
          new PageImpl<>(Lists.newArrayList(deletedFileDto)),
          Handlers.DELETED_FILE.getHandlerName());
      this.messagePublishingService.publish(user, data);
    });
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
      this.messagePublishingService.publish(userNickName,
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
}
