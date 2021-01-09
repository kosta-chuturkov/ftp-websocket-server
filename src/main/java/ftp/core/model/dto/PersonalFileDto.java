package ftp.core.model.dto;

import ftp.core.model.entities.File;
import org.springframework.beans.factory.annotation.Value;

/**
 * Contains info about an uploaded file
 */
public interface PersonalFileDto extends DataTransferObject {
    @Value("#{target.fileSize}")
    long getSize();

    @Value("#{target.name}")
    String getName();

    @Value("#{target.createdDate}")
    String getTimestamp();

    @Value("#{target.downloadHash}")
    String getDownloadHash();

    @Value("#{target.createdBy.nickName}")
    String getSharingUserName();

    @Value("#{target.fileType}")
    File.FileType getFileType();
    String getDeleteHash();
}
