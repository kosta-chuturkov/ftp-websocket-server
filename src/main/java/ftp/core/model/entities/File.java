package ftp.core.model.entities;

import com.google.common.collect.Sets;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Document(indexName = "ftp.server", type = "file")
public class File extends AbstractEntity<Long> implements Serializable{

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User creator;

  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "download_hash", unique = true, length = 64)
  private String downloadHash;

  @NotNull
  @Column(name = "delete_hash", unique = true, length = 64)
  private String deleteHash;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "createdDate")
  private Date createdDate;

  @Column(name = "updatedDate")
  private Date updatedDate;

  @NotNull
  @Enumerated
  @Column(name = "filetype")
  private FileType fileType;

  public File() {

  }

  private File(Builder builder) {
    setCreator(builder.creator);
    setName(builder.name);
    setDownloadHash(builder.downloadHash);
    setDeleteHash(builder.deleteHash);
    setFileSize(builder.fileSize);
    setCreatedDate(builder.timestamp);
    setFileType(builder.fileType);
  }

  public User getCreator() {
    return this.creator;
  }

  public void setCreator(final User creator) {
    this.creator = creator;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDownloadHash() {
    return this.downloadHash;
  }

  public void setDownloadHash(final String downloadHash) {
    this.downloadHash = downloadHash;
  }

  public String getDeleteHash() {
    return this.deleteHash;
  }

  public void setDeleteHash(final String deleteHash) {
    this.deleteHash = deleteHash;
  }

  public Long getFileSize() {
    return this.fileSize;
  }

  public void setFileSize(final Long fileSize) {
    this.fileSize = fileSize;
  }

  public Date getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
  }

  public FileType getFileType() {
    return this.fileType;
  }

  public void setFileType(final FileType fileType) {
    this.fileType = fileType;
  }

  public Date getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }


  public enum FileType {
    SHARED, PRIVATE
  }


  public static final class Builder {

    private User creator;
    private String name;
    private String downloadHash;
    private String deleteHash;
    private long fileSize;
    private Date timestamp;
    private FileType fileType;

    public Builder() {
    }

    public Builder(File copy) {
      this.creator = copy.creator;
      this.name = copy.name;
      this.downloadHash = copy.downloadHash;
      this.deleteHash = copy.deleteHash;
      this.fileSize = copy.fileSize;
      this.timestamp = copy.createdDate;
      this.fileType = copy.fileType;
    }

    public Builder withCreator(User val) {
      this.creator = val;
      return this;
    }

    public Builder withName(String val) {
      this.name = val;
      return this;
    }

    public Builder withDownloadHash(String val) {
      this.downloadHash = val;
      return this;
    }

    public Builder withDeleteHash(String val) {
      this.deleteHash = val;
      return this;
    }

    public Builder withFileSize(long val) {
      this.fileSize = val;
      return this;
    }

    public Builder withTimestamp(Date val) {
      this.timestamp = val;
      return this;
    }

    public Builder withFileType(FileType val) {
      this.fileType = val;
      return this;
    }

    public File build() {
      return new File(this);
    }
  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        File file = (File) o;
        return Objects.equals(creator, file.creator) &&
                Objects.equals(name, file.name) &&
                Objects.equals(downloadHash, file.downloadHash) &&
                Objects.equals(deleteHash, file.deleteHash) &&
                Objects.equals(fileSize, file.fileSize) &&
                Objects.equals(createdDate, file.createdDate) &&
                Objects.equals(updatedDate, file.updatedDate) &&
                fileType == file.fileType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), creator, name, downloadHash, deleteHash, fileSize, createdDate, updatedDate, fileType);
    }
}
