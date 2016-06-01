package ftp.core.common.model.dto;

import ftp.core.common.model.File;

/**
 * Created by Kosta_Chuturkov on 3/30/2016.
 */
public class MainPageFileDto implements FileDto {
    private String sharingUserName;

    private String name;

    private String downloadHash;

    private String deleteHash;

    private long size;

    private String timestamp;

    private File.FileType fileType;


    public MainPageFileDto() {

    }

    public MainPageFileDto(final String sharingUserName, final String name, final String downloadHash, final String deleteHash, final long size,
                           final String timestamp, final File.FileType modifier) {
        this.sharingUserName = sharingUserName;
        this.name = name;
        this.downloadHash = downloadHash;
        this.deleteHash = deleteHash;
        this.size = size;
        this.timestamp = timestamp;
        this.fileType = modifier;
    }


    public String getSharingUserName() {
        return this.sharingUserName;
    }

    public void setSharingUserName(final String sharingUserName) {
        this.sharingUserName = sharingUserName;
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

    public long getSize() {
        return this.size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    public File.FileType getFileType() {
        return this.fileType;
    }

    public void setFileType(final File.FileType modifier) {
        this.fileType = modifier;
    }
}