package ftp.core.model.dto;

import ftp.core.model.entities.File;
import org.springframework.beans.factory.annotation.Value;

/**
 * Contains information about a shared file
 */
public interface SharedFileDto extends DataTransferObject {

  long getSize();

  String getName();

  String getTimestamp();

  String getDownloadHash();

  @Value("#{target.creator.nickName}")
  String getSharingUserName();

  File.FileType getFileType();
}
