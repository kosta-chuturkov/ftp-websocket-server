package ftp.core.service.impl;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ftp.core.api.MessagePublishingService;
import ftp.core.constants.ServerConstants;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.dto.DeletedFileDto;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.dto.SharedFileWithMeDto;
import ftp.core.model.entities.File;
import ftp.core.model.entities.File.FileType;
import ftp.core.model.entities.User;
import ftp.core.repository.FileRepository;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.generic.AbstractGenericService;
import ftp.core.util.DtoUtil;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.handler.Handlers;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service("fileService")
@Transactional
public class FileServiceImpl extends AbstractGenericService<File, Long> implements FileService {

  @Resource
  private FileRepository fileRepository;

  @Resource
  private UserService userService;

  @Resource
  private Gson gson;

  @Resource
  private MessagePublishingService messagePublishingService;

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
    final File savedFile = save(fileToBeSaved);
    if (savedFile != null) {
      this.userService
          .updateRemainingStorageForUser(fileSize, currentUser.getEmail(), remainingStorage);
      this.userService.addFileToUser(savedFile.getId(), currentUser.getEmail());
      if (!validatedUsers.isEmpty()) {
        SharedFileWithMeDto data = DtoUtil.toSharedFileWithMeDto(savedFile);
        String dataToJson = this.gson.toJson(data);
        validatedUsers.forEach(user -> this.messagePublishingService.publish(user, new JsonResponse(dataToJson,
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
    final File exists = findOne(fileId);
    if (exists == null) {
      return false;
    }
    final Set<String> sharedWithUsers = exists.getSharedWithUsers();
    return sharedWithUsers.contains(nickName);
  }

  @Override
  public boolean isFileCreator(final Long fileId, final String userNickName) {
    final File exists = findOne(fileId);
    if (exists == null) {
      return false;
    }
    final User userByNickName = this.userService.getUserByNickName(userNickName);
    return exists.getCreator().getNickName().equals(userByNickName.getNickName());
  }

  @Override
  public List<File> getFilesISharedWithOtherUsers(final String userNickName, final int firstResult,
      final int maxResults) {
    return this.fileRepository.findByCreatorNickNameAndFileType(userNickName, FileType.SHARED,
        new PageRequest(firstResult, maxResults));
  }

  @Override
  public List<File> getPrivateFilesForUser(final String userNickName, final int firstResult,
      final int maxResults) {
    return this.fileRepository.findByCreatorNickNameAndFileType(userNickName, FileType.PRIVATE,
        new PageRequest(firstResult, maxResults));
  }

  @Override
  public List<File> getSharedFilesWithMe(String userNickName, int firstResult, int maxResults) {
    return this.fileRepository.findSharedFilesWithMe(userNickName, FileType.SHARED,
        new PageRequest(firstResult, maxResults));
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
    save(file);
    String dataToJson = this.gson.toJson(new DeletedFileDto(downloadHash));
    persistentSharedToUsers.forEach(user -> {
      JsonResponse data = new JsonResponse(dataToJson, Handlers.DELETED_FILE.getHandlerName());
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
      this.messagePublishingService.publish(userNickName, new JsonResponse(this.gson.toJson(fileDto),
          Handlers.FILES_SHARED_WITH_ME_HANDLER.getHandlerName()));
    }
  }
}
