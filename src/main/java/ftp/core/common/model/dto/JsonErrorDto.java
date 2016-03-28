package ftp.core.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Kosta_Chuturkov on 3/28/2016.
 */
public class JsonErrorDto extends AbstractJsonResponceDto {

    @JsonProperty("error")
    private String error;

    public JsonErrorDto() {
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }
}
