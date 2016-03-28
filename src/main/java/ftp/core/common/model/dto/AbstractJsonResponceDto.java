package ftp.core.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Kosta_Chuturkov on 3/28/2016.
 */
public abstract class AbstractJsonResponceDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("size")
    private String size;

    @JsonProperty("url")
    private String url;

    public AbstractJsonResponceDto() {

    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
