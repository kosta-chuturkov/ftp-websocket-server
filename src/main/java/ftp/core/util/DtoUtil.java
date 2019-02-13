package ftp.core.util;

import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.entities.File;
import ftp.core.model.entities.File.FileType;

/**
 * Created by Kosta_Chuturkov on 10/13/2016.
 */
public final class DtoUtil {

  private DtoUtil() {

  }

  public static SharedFileDto toSharedFileWithMeDto(File file) {
    return new SharedFileDtoImpl(file.getFileSize(),
        file.getName(),
        file.getCreatedDate().toString(),
        file.getDownloadHash(),
        file.getCreator().getNickName(),
        file.getFileType());
  }

  public static class SharedFileDtoImpl implements SharedFileDto {

    private long size;

    private String name;

    private String timestamp;

    private String downloadHash;

    private String sharingUserName;

    private File.FileType fileType;

    public SharedFileDtoImpl(long size, String name, String timestamp, String downloadHash,
        String sharingUserName, FileType fileType) {
      this.size = size;
      this.name = name;
      this.timestamp = timestamp;
      this.downloadHash = downloadHash;
      this.sharingUserName = sharingUserName;
      this.fileType = fileType;
    }

    @Override
    public long getSize() {
      return size;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getTimestamp() {
      return timestamp;
    }

    @Override
    public String getDownloadHash() {
      return downloadHash;
    }

    @Override
    public String getSharingUserName() {
      return sharingUserName;
    }

    @Override
    public FileType getFileType() {
      return fileType;
    }
  }

}
