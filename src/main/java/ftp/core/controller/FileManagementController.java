package ftp.core.controller;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import ftp.core.constants.APIAliases;
import ftp.core.model.dto.DeletedFilesDto;
import ftp.core.model.dto.FileWithSharedUsersWithMeDto;
import ftp.core.model.dto.JsonFileDto;
import ftp.core.model.dto.PrivateFileWithMeDto;
import ftp.core.model.dto.SharedFileWithMeDto;
import ftp.core.model.dto.UploadedFilesDto;
import ftp.core.model.entities.User;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import ftp.core.service.impl.SchedulingService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

@RestController("fileManagementController")
public class FileManagementController {

  private FileManagementService fileManagementService;

  private Gson gson;

  @Resource
  private SchedulingService schedulingService;

  @Autowired
  public FileManagementController(FileManagementService fileManagementService, Gson gson) {
    this.fileManagementService = fileManagementService;
    this.gson = gson;
  }

  @Secured(Authorities.USER)
  @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS}, method = RequestMethod.POST)
  public DeferredResult<String> updateProfilePicture(
      @RequestParam("files[]") final MultipartFile file) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService.updateProfilePicture(file), 10000L);
  }

  @Secured(Authorities.USER)
  @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS + "*"}, method = RequestMethod.POST)
  public DeferredResult<UploadedFilesDto<JsonFileDto>> uploadFile(
      @RequestParam("files[]") final MultipartFile file,
      @RequestParam("userNickNames") final String nickNamesAsString) {
    return this.schedulingService
        .scheduleTask(() -> {
          Set<String> userNickNames = this.gson.fromJson(nickNamesAsString, HashSet.class);
          return this.fileManagementService
              .uploadFile(file, userNickNames == null ? Sets.newHashSet() : userNickNames);
        }, null);
  }

  @Secured(Authorities.USER)
  @RequestMapping(value = {APIAliases.DELETE_FILE_ALIAS}, method = RequestMethod.GET)
  public DeferredResult<DeletedFilesDto> deleteFiles(
      @NotNull @PathVariable final String deleteHash) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService.deleteFiles(deleteHash), 10000L);
  }

  @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS + "{userName}"}, method = RequestMethod.GET)
  public FileSystemResource getProfilePic(@NotNull @PathVariable String userName) {
    return this.fileManagementService.sendProfilePicture(userName);
  }

  @Secured(Authorities.USER)
  @RequestMapping(value = {
      APIAliases.DOWNLOAD_FILE_ALIAS + "{downloadHash}"}, method = RequestMethod.GET)
  public FileSystemResource downloadFile(
      @NotNull @PathVariable String downloadHash) {
    return this.fileManagementService.downloadFile(downloadHash);
  }

  @Secured(Authorities.USER)
  @RequestMapping(value = {APIAliases.GET_FILES_SHARED_WITH_ME_ALIAS}, method = RequestMethod.POST)
  public DeferredResult<List<SharedFileWithMeDto>> getSharedFilesForUser(
      @NotNull @ModelAttribute("firstResult") final Integer firstResult,
      @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService
            .getFilesSharedToMe(firstResult, maxResults, User.getCurrent().getNickName()), 10000L);
  }

  @Secured(Authorities.USER)
  @RequestMapping(value = {APIAliases.GET_PRIVATE_FILES_ALIAS}, method = RequestMethod.POST)
  public DeferredResult<List<PrivateFileWithMeDto>> getPrivateFilesForUser(
      @NotNull @ModelAttribute("firstResult") final Integer firstResult,
      @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService
            .getPrivateFiles(firstResult, maxResults, User.getCurrent().getNickName()), 10000L);
  }

  @Secured(Authorities.USER)
  @RequestMapping(value = {APIAliases.GET_UPLOADED_FILES_ALIAS}, method = RequestMethod.POST)
  public DeferredResult<List<FileWithSharedUsersWithMeDto>> getUploadedFilesByUser(
      @NotNull @ModelAttribute("firstResult") final Integer firstResult,
      @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
    return this.schedulingService
        .scheduleTask(() -> this.fileManagementService
            .getFilesISharedWithOtherUsers(firstResult, maxResults,
                User.getCurrent().getNickName()), 10000L);
  }
}
