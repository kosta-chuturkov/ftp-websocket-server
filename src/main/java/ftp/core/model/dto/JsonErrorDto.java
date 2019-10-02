package ftp.core.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Kosta_Chuturkov on 3/28/2016.
 */
public class JsonErrorDto extends BaseFileDto {

    @JsonProperty("error")
    private String error;

    public JsonErrorDto() {

    }

    private JsonErrorDto(Builder builder) {
        setUrl(builder.url);
        setName(builder.name);
        setSize(builder.size);
        setError(builder.error);
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public static final class Builder {

        private String url;
        private String name;
        private String size;
        private String error;

        public Builder() {
        }

        public Builder(JsonErrorDto copy) {
            this.url = copy.url;
            this.name = copy.name;
            this.size = copy.size;
            this.error = copy.error;
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

        public Builder withError(String val) {
            this.error = val;
            return this;
        }

        public JsonErrorDto build() {
            return new JsonErrorDto(this);
        }
    }
}
