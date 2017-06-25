package ftp.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "ftpserver", ignoreUnknownFields = false)
public class FtpConfigurationProperties {

    private final Storage storage = new Storage();



    public Storage getStorage() {
        return this.storage;
    }

    public static class Storage {
        private String rootStorageFolderName = "ServerFileStorage";

        private String profilePicturesFolderName = "ProfilePictures";

        public String getProfilePicturesFolderName() {
            return this.profilePicturesFolderName;
        }

        public void setProfilePicturesFolderName(String profilePicturesFolderName) {
            this.profilePicturesFolderName = profilePicturesFolderName;
        }

        public String getRootStorageFolderName() {
            return this.rootStorageFolderName;
        }

        public void setRootStorageFolderName(String rootStorageFolderName) {
            this.rootStorageFolderName = rootStorageFolderName;
        }
    }
}
