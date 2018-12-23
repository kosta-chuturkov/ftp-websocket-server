package ftp.core.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import ftp.core.model.dto.DeletedFilesDto;
import ftp.core.model.dto.FileSharedWithUsersDto;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.PersonalFileDto;
import ftp.core.model.dto.SharedFileDto;
import ftp.core.model.dto.UploadedFilesDto;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.impl.SchedulingService;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

@RestController("fileManagementController")
@RequestMapping(path = FileManagementController.PATH, produces = APPLICATION_JSON_VALUE)
public class FileManagementController {

  public static final String PATH = "/api/v1/files";

  private SchedulingService schedulingService;
  private FileManagementService fileManagementService;

  @Autowired
  public FileManagementController(FileManagementService fileManagementService, SchedulingService schedulingService) {
    this.fileManagementService = fileManagementService;
    this.schedulingService = schedulingService;
  }

  @Secured(Authorities.USER)
  @PostMapping(path = "/upload")
  public DeferredResult<UploadedFilesDto<JsonFileDto>> uploadFile(
      @RequestParam("files[]") final MultipartFile file,
      @RequestParam("userNickNames") final String nickNamesAsString) {
    return this.schedulingService.scheduleTask(() -> {
      UploadedFilesDto<JsonFileDto> uploadedFilesDto = this.fileManagementService
          .uploadFile(file, nickNamesAsString);
      return uploadedFilesDto;
        }, null);
  }

  @Secured(Authorities.USER)
  @DeleteMapping(path = "/{deleteHash}")
  public DeferredResult<DeletedFilesDto> deleteFile(
      @NotNull @PathVariable final String deleteHash) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService.deleteFiles(deleteHash), 10000L);
  }

  @Secured(Authorities.USER)
  @GetMapping(path = "/{downloadHash}")
  public FileSystemResource downloadFile(
      @NotNull @PathVariable String downloadHash) {
    return this.fileManagementService.downloadFile(downloadHash);
  }

  @Secured(Authorities.USER)
  @GetMapping(path = "/shared")
  public DeferredResult<Page<SharedFileDto>> getSharedFilesForUser(Pageable pageable) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService
            .getFilesSharedToMe(pageable), 10000L);
  }

  @Secured(Authorities.USER)
  @GetMapping(path = "/private")
  public DeferredResult<Page<PersonalFileDto>> getPrivateFilesForUser(Pageable pageable) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService
            .getPrivateFiles(pageable), 10000L);
  }

  @Secured(Authorities.USER)
  @GetMapping(path = "/uploaded")
  public DeferredResult<Page<FileSharedWithUsersDto>> getUploadedFilesByUser(Pageable pageable) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService
            .getFilesISharedWithOtherUsers(pageable), 10000L);
  }
}
