package ftp.core.model.dto;

import ftp.core.model.entities.File;

/**
 * Contains info about an uploaded file
 */
public class PrivateFileWithMeDto extends SharedFileWithMeDto implements DataTransferObject {

    private String deleteHash;

    protected PrivateFileWithMeDto() {
    }

    private PrivateFileWithMeDto(Builder builder) {
        setSize(builder.size);
        setName(builder.name);
        setTimestamp(builder.timestamp);
        setDownloadHash(builder.downloadHash);
        setSharingUserName(builder.sharingUserName);
        setFileType(builder.fileType);
        setDeleteHash(builder.deleteHash);
    }


    public String getDeleteHash() {
        return this.deleteHash;
    }

    protected void setDeleteHash(final String deleteHash) {
        this.deleteHash = deleteHash;
    }

    public static final class Builder {
        private long size;
        private String name;
        private String timestamp;
        private String downloadHash;
        private String sharingUserName;
        private File.FileType fileType;
        private String deleteHash;

        public Builder() {
        }

        public Builder(PrivateFileWithMeDto copy) {
            this.size = copy.size;
            this.name = copy.name;
            this.timestamp = copy.timestamp;
            this.downloadHash = copy.downloadHash;
            this.sharingUserName = copy.sharingUserName;
            this.fileType = copy.fileType;
            this.deleteHash = copy.deleteHash;
        }

        public Builder withSize(long val) {
            size = val;
            return this;
        }

        public Builder withName(String val) {
            name = val;
            return this;
        }

        public Builder withTimestamp(String val) {
            timestamp = val;
            return this;
        }

        public Builder withDownloadHash(String val) {
            downloadHash = val;
            return this;
        }

        public Builder withSharingUserName(String val) {
            sharingUserName = val;
            return this;
        }

        public Builder withFileType(File.FileType val) {
            fileType = val;
            return this;
        }

        public Builder withDeleteHash(String val) {
            deleteHash = val;
            return this;
        }

        public PrivateFileWithMeDto build() {
            return new PrivateFileWithMeDto(this);
        }
    }
}
