package ftp.core.model.dto;

/**
 * Contains info about an uploaded file
 */
public interface PersonalFileDto extends SharedFileDto, DataTransferObject {
    String getDeleteHash();
}
