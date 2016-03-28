package ftp.core.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Kosta_Chuturkov on 3/28/2016.
 */
public class JsonFileResponseDtoWrapper {

    @JsonProperty("files")
    private final AbstractJsonResponceDto abstractJsonResponceDto;

    @JsonProperty("storedBytes")
    private String storedBytes;

    public JsonFileResponseDtoWrapper(final AbstractJsonResponceDto abstractJsonResponceDto) {
        this.abstractJsonResponceDto = abstractJsonResponceDto;
    }

    public AbstractJsonResponceDto getAbstractJsonResponceDto() {
        return this.abstractJsonResponceDto;
    }

    public String getStoredBytes() {
        return this.storedBytes;
    }

    public void setStoredBytes(final String storedBytes) {
        this.storedBytes = storedBytes;
    }

    public static class Builder {
        private String name;

        private String size;

        private String url;

        private String thumbnailUrl;

        private String deleteUrl;

        private String deleteType;

        private String error;

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder size(final String size) {
            this.size = size;
            return this;
        }

        public Builder url(final String url) {
            this.url = url;
            return this;
        }

        public Builder thumbnailUrl(final String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder deleteUrl(final String deleteUrl) {
            this.deleteUrl = deleteUrl;
            return this;
        }

        public Builder deleteType(final String deleteType) {
            this.deleteType = deleteType;
            return this;
        }

        public Builder error(final String error) {
            this.error = error;
            return this;
        }

        public JsonFileResponseDtoWrapper build() {
            if (this.error != null) {
                return new JsonFileResponseDtoWrapper(getJsonErrorDto());
            } else {
                return new JsonFileResponseDtoWrapper(getJsonFileDto());
            }
        }

        private JsonFileDto getJsonFileDto() {
            final JsonFileDto jsonFileDto = new JsonFileDto();
            jsonFileDto.setName(this.name);
            jsonFileDto.setDeleteType(this.deleteType);
            jsonFileDto.setDeleteUrl(this.deleteUrl);
            jsonFileDto.setThumbnailUrl(this.thumbnailUrl);
            jsonFileDto.setSize(this.size);
            jsonFileDto.setUrl(this.url);
            return jsonFileDto;
        }

        private JsonErrorDto getJsonErrorDto() {
            final JsonErrorDto jsonErrorDto = new JsonErrorDto();
            jsonErrorDto.setError(this.error);
            jsonErrorDto.setName(this.name);
            jsonErrorDto.setSize(this.size);
            jsonErrorDto.setUrl(this.url);
            return jsonErrorDto;
        }
    }
}
