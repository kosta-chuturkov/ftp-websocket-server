package ftp.core.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Adapts the dtos to the required model by the front end. The file must be under the property named
 * files.
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

    private BaseFileDto baseFileDto;
    private String storedBytes;

    public Builder() {
    }

    public Builder(ResponseModelAdapter copy) {
      this.baseFileDto = copy.baseFileDto;
      this.storedBytes = copy.storedBytes;
    }

    public Builder withBaseFileDto(BaseFileDto val) {
      this.baseFileDto = val;
      return this;
    }

    public Builder withStoredBytes(String val) {
      this.storedBytes = val;
      return this;
    }

    public ResponseModelAdapter build() {
      return new ResponseModelAdapter(this);
    }
  }
}
