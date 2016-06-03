package ftp.core.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kosta on 3.6.2016 г..
 */
public class DeletedFileDto {

    @JsonProperty("deletedFileUid")
    private String deletedFileUid;

    public DeletedFileDto(final String deletedFileUid) {
        this.deletedFileUid = deletedFileUid;
    }

    public DeletedFileDto() {
    }

    public String getDeletedFileUid() {
        return this.deletedFileUid;
    }

    public void setDeletedFileUid(final String deletedFileUid) {
        this.deletedFileUid = deletedFileUid;
    }
}
