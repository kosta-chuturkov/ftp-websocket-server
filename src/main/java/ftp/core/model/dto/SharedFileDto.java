package ftp.core.model.dto;

import ftp.core.model.entities.File;
import org.springframework.beans.factory.annotation.Value;

/**
 * Contains information about a shared file
 */
public interface SharedFileDto extends DataTransferObject {
    @Value("#{target.file.fileSize}")
    long getSize();

    @Value("#{target.file.name}")
    String getName();

    @Value("#{target.file.createdDate}")
    String getTimestamp();

    @Value("#{target.file.downloadHash}")
    String getDownloadHash();

    @Value("#{target.file.creator.nickName}")
    String getSharingUserName();

    @Value("#{target.file.fileType}")
    File.FileType getFileType();
}
