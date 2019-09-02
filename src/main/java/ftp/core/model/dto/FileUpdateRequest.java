package ftp.core.model.dto;

import com.google.common.collect.Sets;
import ftp.core.model.entities.File;
import ftp.core.model.entities.File.FileType;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Request to update a given file`s properties
 */
public class FileUpdateRequest {

  @NotNull
  @Size(min = 1, max = 255)
  private String name;

  @NotNull
  @Size(min = 64, max = 64)
  private String downloadHash;

  @NotNull
  private File.FileType fileType;

  private Set<String> sharedWithUsers = Sets.newHashSet();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDownloadHash() {
    return downloadHash;
  }

  public void setDownloadHash(String downloadHash) {
    this.downloadHash = downloadHash;
  }

  public FileType getFileType() {
    return fileType;
  }

  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }

  public Set<String> getSharedWithUsers() {
    return sharedWithUsers;
  }

  public void setSharedWithUsers(Set<String> sharedWithUsers) {
    this.sharedWithUsers = sharedWithUsers;
  }
}
