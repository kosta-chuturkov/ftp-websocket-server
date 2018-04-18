package ftp.core.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by Kosta_Chuturkov on 11/3/2016.
 */
public class UploadedFilesDto<T extends BaseFileDto> {

  @JsonProperty("files")
  private List<T> files;

  public UploadedFilesDto(List<T> files) {
    this.files = files;
  }

  public List<T> getFiles() {
    return this.files;
  }
}
