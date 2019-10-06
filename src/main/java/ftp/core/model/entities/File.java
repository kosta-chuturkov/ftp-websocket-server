package ftp.core.model.entities;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "files", indexes = {@Index(name = "ftp_server_file_type_index", columnList = "fileType"),
        @Index(name = "ftp_server_search_string_index", columnList = "searchString")})
@EntityListeners(AuditingEntityListener.class)
public class File extends AuditEntity<Long> implements Serializable {

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

    @NotNull
    @Enumerated
    @Column(name = "filetype")
    private FileType fileType;

    @Column(length = 1000)
    private String searchString;


    @PreUpdate
    @PrePersist
    void updateSearchString() {
        final String fullSearchString = StringUtils.join(Arrays.asList(
                this.name,
                this.fileSize,
                this.fileType,
                this.getCreatedBy().getNickName()),
                " ");
        this.searchString = StringUtils.substring(fullSearchString, 0, 999).toLowerCase();
    }

    public File() {

    }

    private File(Builder builder) {
        setName(builder.name);
        setDownloadHash(builder.downloadHash);
        setDeleteHash(builder.deleteHash);
        setFileSize(builder.fileSize);
        setCreatedDate(builder.timestamp);
        setFileType(builder.fileType);
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

    public FileType getFileType() {
        return this.fileType;
    }

    public void setFileType(final FileType fileType) {
        this.fileType = fileType;
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
            this.name = copy.name;
            this.downloadHash = copy.downloadHash;
            this.deleteHash = copy.deleteHash;
            this.fileSize = copy.fileSize;
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

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        File file = (File) o;
        return Objects.equals(name, file.name) &&
                Objects.equals(downloadHash, file.downloadHash) &&
                Objects.equals(deleteHash, file.deleteHash) &&
                Objects.equals(fileSize, file.fileSize) &&
                fileType == file.fileType &&
                Objects.equals(searchString, file.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, downloadHash, deleteHash, fileSize, fileType, searchString);
    }
}
