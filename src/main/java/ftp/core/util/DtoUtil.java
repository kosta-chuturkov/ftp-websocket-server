package ftp.core.util;

import ftp.core.model.dto.FileWithSharedUsersWithMeDto;
import ftp.core.model.dto.PrivateFileWithMeDto;
import ftp.core.model.dto.SharedFileWithMeDto;
import ftp.core.model.entities.File;

/**
 * Created by Kosta_Chuturkov on 10/13/2016.
 */
public final class DtoUtil {

  private DtoUtil() {

  }

  public static PrivateFileWithMeDto toPrivateFileDto(File file) {
    return new PrivateFileWithMeDto.Builder()
        .withSharingUserName(file.getCreator().getNickName())
        .withName(file.getName())
        .withDownloadHash(file.getDownloadHash())
        .withDeleteHash(file.getDeleteHash())
        .withSize(file.getFileSize())
        .withTimestamp(file.getTimestamp().toString())
        .withFileType(file.getFileType())
        .build();
  }

  public static FileWithSharedUsersWithMeDto toSharedFileWithOtherUsersDto(File file) {
    FileWithSharedUsersWithMeDto dto = new FileWithSharedUsersWithMeDto.Builder()
        .withSharingUserName(file.getCreator().getNickName())
        .withName(file.getName())
        .withDownloadHash(file.getDownloadHash())
        .withDeleteHash(file.getDeleteHash())
        .withSize(file.getFileSize())
        .withTimestamp(file.getTimestamp().toString())
        .withFileType(file.getFileType())
        .build();

    file.getSharedWithUsers()
        .stream()
        .forEach(s -> dto.addSharedUser(s));

    return dto;
  }

  public static SharedFileWithMeDto toSharedFileWithMeDto(File file) {
    return new SharedFileWithMeDto.Builder()
        .withSharingUserName(file.getCreator().getNickName())
        .withName(file.getName())
        .withDownloadHash(file.getDownloadHash())
        .withSize(file.getFileSize())
        .withTimestamp(file.getTimestamp().toString())
        .withFileType(file.getFileType())
        .build();
  }


}
