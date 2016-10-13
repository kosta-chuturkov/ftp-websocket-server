package ftp.core.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Adapts the dtos to the required model by the front end.
 * The file must be under the property named files.
 */
public class ResponseModelAdapter {

    @JsonProperty("files")
    private BaseFileDto baseFileDto;
    @JsonProperty("storedBytes")
    private String storedBytes;

    ResponseModelAdapter() {

    }

    private ResponseModelAdapter(Builder builder) {
        this.baseFileDto = builder.baseFileDto;
        this.storedBytes = builder.storedBytes;
    }

    public String getStoredBytes() {
        return this.storedBytes;
    }

    public static final class Builder {
        private final BaseFileDto baseFileDto;
        private String storedBytes;

        public Builder(BaseFileDto baseFileDto) {
            this.baseFileDto = baseFileDto;
        }

        public Builder(ResponseModelAdapter copy) {
            this.baseFileDto = copy.baseFileDto;
            this.storedBytes = copy.storedBytes;
        }

        public Builder withStoredBytes(String val) {
            this.storedBytes = val;
            return this;
        }

        public ResponseModelAdapter build() {
            return new ResponseModelAdapter(this);
        }
    }


//    private JsonFileDto getJsonFileDto() {
//        final JsonFileDto jsonFileDto = new JsonFileDto(this.name, this.size, this.url);
//        jsonFileDto.setDeleteType(this.deleteType);
//        jsonFileDto.setDeleteUrl(this.deleteUrl);
//        jsonFileDto.setThumbnailUrl(this.thumbnailUrl);
//        return jsonFileDto;
//    }
//
//    private JsonErrorDto getJsonErrorDto() {
//        final JsonErrorDto jsonErrorDto = new JsonErrorDto(this.name, this.size, this.url);
//        jsonErrorDto.setError(this.error);
//        return jsonErrorDto;
//    }
}
