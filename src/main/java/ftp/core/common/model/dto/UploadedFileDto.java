package ftp.core.common.model.dto;

import ftp.core.common.model.File;

/**
 * Created by Kosta_Chuturkov on 3/30/2016.
 */
public class UploadedFileDto extends SharedFileDto implements AbstractDto {
    private String deleteHash;


    public UploadedFileDto() {
        super();
    }

    public UploadedFileDto(final String sharingUserName, final String name, final String downloadHash, final String deleteHash, final long size,
                           final String timestamp, final File.FileType modifier) {
        super(sharingUserName, name, downloadHash, size, timestamp, modifier);
        this.deleteHash = deleteHash;
    }


    public String getDeleteHash() {
        return this.deleteHash;
    }

    public void setDeleteHash(final String deleteHash) {
        this.deleteHash = deleteHash;
    }

}
