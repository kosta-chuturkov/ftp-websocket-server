package ftp.core.controller;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ftp.core.constants.APIAliases;
import ftp.core.model.dto.*;
import ftp.core.model.entities.User;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.impl.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController("fileManagementController")
public class FileManagementController {

    private FileManagementService fileManagementService;

    private Gson gson;

    @Resource
    private EventService eventService;

    @Autowired
    public FileManagementController(FileManagementService fileManagementService, Gson gson) {
        this.fileManagementService = fileManagementService;
        this.gson = gson;
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS}, method = RequestMethod.POST)
    public DeferredResult<String> updateProfilePicture(@RequestParam("files[]") final MultipartFile file) {
        return this.eventService
                .scheduleTaskToReactor(() -> this.fileManagementService.updateProfilePicture(file), 10000L);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS + "*"}, method = RequestMethod.POST)
    public DeferredResult<UploadedFilesDto<JsonFileDto>> uploadFile(@RequestParam("files[]") final MultipartFile file,
                                                                    @RequestParam("userNickNames") final String nickNamesAsString) {
        return this.eventService
                .scheduleTaskToReactor(() -> {
                    Set<String> userNickNames = this.gson.fromJson(nickNamesAsString, HashSet.class);
                    return this.fileManagementService.uploadFile(file, userNickNames == null ? Sets.newHashSet() : userNickNames);
                }, null);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DELETE_FILE_ALIAS}, method = RequestMethod.GET)
    public DeferredResult<DeletedFilesDto> deleteFiles(@NotNull @PathVariable final String deleteHash) {
        return this.eventService
                .scheduleTaskToReactor(() -> this.fileManagementService.deleteFiles(deleteHash), 10000L);
    }

    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS + "{userName}"}, method = RequestMethod.GET)
    public DeferredResult<FileSystemResource> getProfilePic(@NotNull @PathVariable String userName) {
        return this.eventService
                .scheduleTaskToReactor(() -> this.fileManagementService.sendProfilePicture(userName), 10000L);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DOWNLOAD_FILE_ALIAS + "{downloadHash}"}, method = RequestMethod.GET)
    public DeferredResult<FileSystemResource> downloadFile(@NotNull @PathVariable String downloadHash) {
        return this.eventService
                .scheduleTaskToReactor(() -> this.fileManagementService.downloadFile(downloadHash), null);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.GET_FILES_SHARED_WITH_ME_ALIAS}, method = RequestMethod.POST)
    public DeferredResult<List<SharedFileWithMeDto>> getSharedFilesForUser(@NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                                           @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
        return this.eventService
                .scheduleTaskToReactor(() -> this.fileManagementService.getFilesSharedToMe(firstResult, maxResults, User.getCurrent().getNickName()), 10000L);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.GET_PRIVATE_FILES_ALIAS}, method = RequestMethod.POST)
    public DeferredResult<List<PrivateFileWithMeDto>> getPrivateFilesForUser(@NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                                             @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
        return this.eventService
                .scheduleTaskToReactor(() -> this.fileManagementService.getPrivateFiles(firstResult, maxResults, User.getCurrent().getNickName()), 10000L);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.GET_UPLOADED_FILES_ALIAS}, method = RequestMethod.POST)
    public DeferredResult<List<FileWithSharedUsersWithMeDto>> getUploadedFilesByUser(@NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                                                     @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
        return this.eventService
                .scheduleTaskToReactor(() -> this.fileManagementService.getFilesISharedWithOtherUsers(firstResult, maxResults, User.getCurrent().getNickName()), 10000L);
    }
}
