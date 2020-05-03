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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@CrossOrigin(origins = "http://localhost")
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
    public UploadedFilesDto<JsonFileDto> uploadFile(
            @RequestParam("files[]") final MultipartFile file,
            @RequestParam("userNickNames") final String nickNamesAsString) {
        return this.fileManagementService.uploadFile(file, nickNamesAsString);

    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "deleteFile")
    @DeleteMapping(path = "/{deleteHash}/delete")
    public DeferredResult<DeletedFilesDto> deleteFile(
            @NotNull @PathVariable final String deleteHash) {
        return this.schedulingService.scheduleTask(() -> this.fileManagementService.deleteFiles(deleteHash), 10000L);
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
    @GetMapping(path = "/upload_mockup_data")
    public DeferredResult<FileSharedToUser> uploadMockupData() {
        return this.schedulingService
                .scheduleTask(() -> this.fileManagementService.uploadMockupData(), 10000L);
    }


    @ApiOperation(value = "", nickname = "getAllFiles")
    @GetMapping(path = "/files")
    public PageResource<File> getAllFiles(@RequestParam(name = "type") String type,
                                          @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", required = false, defaultValue = "50") Integer size) {
        return new PageResource<>(this.fileManagementService.getAllFiles(PageRequest.of(page, size), type));
    }

    @ApiOperation(value = "", nickname = "findByQuery")
    @GetMapping(path = "/files/search")
    public PageResource<File> findByQuery(@RequestParam(name = "q") String query,
                                          @RequestParam(name = "type") String type,
                                          @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", required = false, defaultValue = "50") Integer size) throws UnsupportedEncodingException {
        return new PageResource<>(this.fileManagementService.findByQuery(URLDecoder.decode(query, StandardCharsets.UTF_8.name()), type, PageRequest.of(page, size)));
    }

}
