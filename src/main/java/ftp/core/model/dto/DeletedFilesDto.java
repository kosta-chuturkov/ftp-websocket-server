package ftp.core.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by Kosta_Chuturkov on 11/3/2016.
 */
public class DeletedFilesDto {

    @JsonProperty("files")
    private Map<String, String> files;
    private String storedBytes;

    public DeletedFilesDto(Map<String, String> files, String storedBytes) {
        this.files = files;
        this.storedBytes = storedBytes;
    }

    public Map<String, String> getFiles() {
        return this.files;
    }

    public String getStoredBytes() {
        return this.storedBytes;
    }
}
