package ftp.core.model.dto;

import ftp.core.model.entities.File;

/**
 * Contains information about a shared file
 */
public class SharedFileWithMeDto implements DataTransferObject {

    protected long size;

    protected String name;

    protected String timestamp;

    protected String downloadHash;

    protected String sharingUserName;

    protected File.FileType fileType;

    protected SharedFileWithMeDto() {
    }

    private SharedFileWithMeDto(Builder builder) {
        setSize(builder.size);
        setName(builder.name);
        setTimestamp(builder.timestamp);
        setDownloadHash(builder.downloadHash);
        setSharingUserName(builder.sharingUserName);
        setFileType(builder.fileType);
    }

    public long getSize() {
        return this.size;
    }

    protected void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    protected void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDownloadHash() {
        return this.downloadHash;
    }

    protected void setDownloadHash(String downloadHash) {
        this.downloadHash = downloadHash;
    }

    public String getSharingUserName() {
        return this.sharingUserName;
    }

    protected void setSharingUserName(String sharingUserName) {
        this.sharingUserName = sharingUserName;
    }

    public File.FileType getFileType() {
        return this.fileType;
    }

    protected void setFileType(File.FileType fileType) {
        this.fileType = fileType;
    }


    public static final class Builder {
        private long size;
        private String name;
        private String timestamp;
        private String downloadHash;
        private String sharingUserName;
        private File.FileType fileType;

        public Builder() {
        }

        public Builder(SharedFileWithMeDto copy) {
            this.size = copy.size;
            this.name = copy.name;
            this.timestamp = copy.timestamp;
            this.downloadHash = copy.downloadHash;
            this.sharingUserName = copy.sharingUserName;
            this.fileType = copy.fileType;
        }

        public Builder withSize(long val) {
            this.size = val;
            return this;
        }

        public Builder withName(String val) {
            this.name = val;
            return this;
        }

        public Builder withTimestamp(String val) {
            this.timestamp = val;
            return this;
        }

        public Builder withDownloadHash(String val) {
            this.downloadHash = val;
            return this;
        }

        public Builder withSharingUserName(String val) {
            this.sharingUserName = val;
            return this;
        }

        public Builder withFileType(File.FileType val) {
            this.fileType = val;
            return this;
        }

        public SharedFileWithMeDto build() {
            return new SharedFileWithMeDto(this);
        }
    }
}
