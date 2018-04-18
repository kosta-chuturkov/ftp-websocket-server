package ftp.core.model.dto;

/**
 * Contains info about deleted files
 */
public class DeletedFileDto implements DataTransferObject {

  private final String deletedFileUid;

  public DeletedFileDto(final String deletedFileUid) {
    this.deletedFileUid = deletedFileUid;
  }

  public String getDeletedFileUid() {
    return this.deletedFileUid;
  }

}
