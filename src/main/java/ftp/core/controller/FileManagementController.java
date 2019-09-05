package ftp.core.controller;

import ftp.core.model.dto.*;
import ftp.core.model.entities.File;
import ftp.core.model.entities.FileSharedToUser;
import ftp.core.rest.PageResource;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.impl.SchedulingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController("fileManagementController")
@RequestMapping(path = FileManagementController.PATH, produces = APPLICATION_JSON_VALUE)
@Api(tags = FileManagementController.TAG)
public class FileManagementController {

    public static final String TAG = "Files";

    public static final String PATH = "/api/v1/files";

    private SchedulingService schedulingService;
    private FileManagementService fileManagementService;

    @Autowired
    public FileManagementController(FileManagementService fileManagementService, SchedulingService schedulingService) {
        this.fileManagementService = fileManagementService;
        this.schedulingService = schedulingService;
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "uploadFile")
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
    @ApiOperation(value = "", nickname = "deleteFile")
    @DeleteMapping(path = "/{deleteHash}/delete")
    public DeferredResult<DeletedFilesDto> deleteFile(
            @NotNull @PathVariable final String deleteHash) {
        return this.schedulingService
                .scheduleTask(() -> this.fileManagementService.deleteFiles(deleteHash), 10000L);
    }


    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "updateFile")
    @PutMapping(path = "/{downloadHash}/update")
    public File updateFile(
            @NotNull @Valid @RequestBody FileUpdateRequest file) {
        return this.fileManagementService.updateFile(file);
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "downloadFile")
    @GetMapping(path = "/{downloadHash}/download")
    public FileSystemResource downloadFile(
            @NotNull @PathVariable String downloadHash) {
        return this.fileManagementService.downloadFile(downloadHash);
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "getSharedFilesForUser")
    @GetMapping(path = "/shared")
    public PageResource<SharedFileDto> getSharedFilesForUser(@RequestParam(required = false, defaultValue = "0") int pageNumber,
                                                             @RequestParam(required = false, defaultValue = "50") int pageSize) {
        return new PageResource<>(this.fileManagementService
                .getFilesSharedToMe(PageRequest.of(pageNumber, pageSize)));
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "getPrivateFilesForUser")
    @GetMapping(path = "/private")
    public PageResource<PersonalFileDto> getPrivateFilesForUser(@RequestParam(required = false, defaultValue = "0") int pageNumber,
                                                                @RequestParam(required = false, defaultValue = "50") int pageSize) {
        return new PageResource<>(this.fileManagementService
                .getPrivateFiles(PageRequest.of(pageNumber, pageSize)));
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "getUploadedFilesByUser")
    @GetMapping(path = "/uploaded")
    public PageResource<FileSharedWithUsersDto> getUploadedFilesByUser(@RequestParam(required = false, defaultValue = "0") int pageNumber,
                                                                       @RequestParam(required = false, defaultValue = "50") int pageSize) {
        return new PageResource<>(this.fileManagementService
                .getFilesISharedWithOtherUsers(PageRequest.of(pageNumber, pageSize)));
    }

    @ApiOperation(value = "", nickname = "test")
    @GetMapping(path = "/test")
    public DeferredResult<FileSharedToUser> test() {
        return this.schedulingService
                .scheduleTask(() -> this.fileManagementService.test(), 10000L);
    }

    @ApiOperation(value = "", nickname = "getAllFiles")
    @GetMapping(path = "/files")
    public List<File> getAllFiles() {
        return this.fileManagementService.getAllFiles();
    }


    @ApiOperation(value = "", nickname = "getAllFiles2")
    @GetMapping(path = "/filess")
    public DeferredResult<List<File>> getAllFiles2() {
        return this.schedulingService
                .scheduleTask(() -> this.fileManagementService.getAllFiles(), 10000L);
    }
}
