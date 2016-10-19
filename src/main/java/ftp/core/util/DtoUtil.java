package ftp.core.util;

import ftp.core.model.dto.FileWithSharedUsersDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.dto.UploadedFileDto;
import ftp.core.model.entities.File;

/**
 * Created by Kosta_Chuturkov on 10/13/2016.
 */
public final class DtoUtil {

    private DtoUtil() {

    }

    public static UploadedFileDto toUploadedFileDto(File file) {
        return new UploadedFileDto.Builder()
                .withSharingUserName(file.getCreator().getNickName())
                .withName(file.getName())
                .withDownloadHash(file.getDownloadHash())
                .withDeleteHash(file.getDeleteHash())
                .withSize(file.getFileSize())
                .withTimestamp(file.getTimestamp().toString())
                .withFileType(file.getFileType())
                .build();
    }

    public static FileWithSharedUsersDto toFileWithSharedUsersDto(File file) {
        FileWithSharedUsersDto dto = new FileWithSharedUsersDto.Builder()
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

    public static SharedFileDto toSharedFileDto(File file) {
        return new SharedFileDto.Builder()
                .withSharingUserName(file.getCreator().getNickName())
                .withName(file.getName())
                .withDownloadHash(file.getDownloadHash())
                .withSize(file.getFileSize())
                .withTimestamp(file.getTimestamp().toString())
                .withFileType(file.getFileType())
                .build();
    }


}
