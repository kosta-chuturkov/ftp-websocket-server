package ftp.core.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Kosta_Chuturkov on 3/28/2016.
 */
public class JsonFileDto extends AbstractJsonResponceDto {

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @JsonProperty("deleteUrl")
    private String deleteUrl;

    @JsonProperty("deleteType")
    private String deleteType;

    public JsonFileDto() {
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public void setThumbnailUrl(final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDeleteUrl() {
        return this.deleteUrl;
    }

    public void setDeleteUrl(final String deleteUrl) {
        this.deleteUrl = deleteUrl;
    }

    public String getDeleteType() {
        return this.deleteType;
    }

    public void setDeleteType(final String deleteType) {
        this.deleteType = deleteType;
    }
}
