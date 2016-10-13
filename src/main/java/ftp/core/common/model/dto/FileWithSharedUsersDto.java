package ftp.core.common.model.dto;

import ftp.core.common.model.File;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Kosta_Chuturkov on 3/30/2016.
 */
public class FileWithSharedUsersDto extends UploadedFileDto {

    private final Set<String> sharedToUsers = new TreeSet<>();

    protected FileWithSharedUsersDto() {
    }

    private FileWithSharedUsersDto(Builder builder) {
        setSize(builder.size);
        setName(builder.name);
        setTimestamp(builder.timestamp);
        setDownloadHash(builder.downloadHash);
        setSharingUserName(builder.sharingUserName);
        setFileType(builder.fileType);
    }


    public Set<String> getSharedToUsers() {
        return this.sharedToUsers;
    }

    public boolean addSharedUser(final String name) {
        if (!this.sharedToUsers.contains(name)) {
            return this.sharedToUsers.add(name);
        }
        return false;
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

        public Builder(FileWithSharedUsersDto copy) {
            this.size = copy.size;
            this.name = copy.name;
            this.timestamp = copy.timestamp;
            this.downloadHash = copy.downloadHash;
            this.sharingUserName = copy.sharingUserName;
            this.fileType = copy.fileType;
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

        public FileWithSharedUsersDto build() {
            return new FileWithSharedUsersDto(this);
        }
    }
}
