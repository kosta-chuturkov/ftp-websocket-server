package ftp.core.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Kosta_Chuturkov on 3/28/2016.
 */
public class JsonFileDto extends BaseFileDto {

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @JsonProperty("deleteUrl")
    private String deleteUrl;

    @JsonProperty("deleteType")
    private String deleteType;


    private JsonFileDto(Builder builder) {
        setUrl(builder.url);
        setName(builder.name);
        setSize(builder.size);
        setThumbnailUrl(builder.thumbnailUrl);
        setDeleteUrl(builder.deleteUrl);
        setDeleteType(builder.deleteType);
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


    public static final class Builder {
        private String url;
        private String name;
        private String size;
        private String thumbnailUrl;
        private String deleteUrl;
        private String deleteType;

        public Builder() {
        }

        public Builder(JsonFileDto copy) {
            this.url = copy.url;
            this.name = copy.name;
            this.size = copy.size;
            this.thumbnailUrl = copy.thumbnailUrl;
            this.deleteUrl = copy.deleteUrl;
            this.deleteType = copy.deleteType;
        }

        public Builder withUrl(String val) {
            this.url = val;
            return this;
        }

        public Builder withName(String val) {
            this.name = val;
            return this;
        }

        public Builder withSize(String val) {
            this.size = val;
            return this;
        }

        public Builder withThumbnailUrl(String val) {
            this.thumbnailUrl = val;
            return this;
        }

        public Builder withDeleteUrl(String val) {
            this.deleteUrl = val;
            return this;
        }

        public Builder withDeleteType(String val) {
            this.deleteType = val;
            return this;
        }

        public JsonFileDto build() {
            return new JsonFileDto(this);
        }
    }
}
