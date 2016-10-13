package ftp.core.common.model.dto;

/**
 * Object containing the base fields for files
 */
public class BaseFileDto implements DataTransferObject {

    private String url;
    private String name;
    private String size;

    public BaseFileDto(String name, String size, String url) {
        this.name = name;
        this.size = size;
        this.url = url;
    }

    public BaseFileDto() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
