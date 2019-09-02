package ftp.core.model.entities;

import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "files")
public class File extends AbstractEntity<Long> implements Serializable{

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "file_shared_to_users", joinColumns = @JoinColumn(name = "file_id"))
  @Column(name = "nickname", length = 32)
  private Set<String> sharedWithUsers = Sets.newHashSet();

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
    setSharedWithUsers(builder.sharedWithUsers);
    setCreator(builder.creator);
    setName(builder.name);
    setDownloadHash(builder.downloadHash);
    setDeleteHash(builder.deleteHash);
    setFileSize(builder.fileSize);
    setCreatedDate(builder.timestamp);
    setFileType(builder.fileType);
  }

  public boolean addUser(final String user) {
    return !this.sharedWithUsers.contains(user) && this.sharedWithUsers.add(user);
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

  public Set<String> getSharedWithUsers() {
    if (this.sharedWithUsers == null) {
      return Sets.newHashSet();
    }
    return this.sharedWithUsers;
  }

  public void setSharedWithUsers(final Set<String> sharedWithUsers) {
    this.sharedWithUsers.clear();
    if (sharedWithUsers != null) {
      this.sharedWithUsers.addAll(sharedWithUsers);
    }
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

    private Set<String> sharedWithUsers;
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
      this.sharedWithUsers = copy.sharedWithUsers;
      this.creator = copy.creator;
      this.name = copy.name;
      this.downloadHash = copy.downloadHash;
      this.deleteHash = copy.deleteHash;
      this.fileSize = copy.fileSize;
      this.timestamp = copy.createdDate;
      this.fileType = copy.fileType;
    }

    public Builder withSharedWithUsers(Set<String> val) {
      this.sharedWithUsers = val;
      return this;
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
    if (this == o) {
      return true;
    }
    if (!(o instanceof File)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    File file = (File) o;
    return Objects.equals(getSharedWithUsers(), file.getSharedWithUsers()) &&
        Objects.equals(getCreator(), file.getCreator()) &&
        Objects.equals(getName(), file.getName()) &&
        Objects.equals(getDownloadHash(), file.getDownloadHash()) &&
        Objects.equals(getDeleteHash(), file.getDeleteHash()) &&
        Objects.equals(getFileSize(), file.getFileSize()) &&
        Objects.equals(getCreatedDate(), file.getCreatedDate()) &&
        getFileType() == file.getFileType();
  }

  @Override
  public int hashCode() {

    return Objects
        .hash(super.hashCode(), getSharedWithUsers(), getCreator(), getName(), getDownloadHash(),
            getDeleteHash(), getFileSize(), getCreatedDate(), getFileType());
  }
}
