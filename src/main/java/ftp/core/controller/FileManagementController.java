package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController("fileManagementController")
public class FileManagementController {

    private FileManagementService fileManagementService;

    @Autowired
    public FileManagementController(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS}, method = RequestMethod.POST)
    public String updateProfilePicture(@RequestParam("files[]") final MultipartFile file) {
        return this.fileManagementService.updateProfilePicture(file);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS + "*"}, method = RequestMethod.POST)
    public String uploadFile(@RequestParam("files[]") final MultipartFile file,
                             @RequestParam("nickName") final String userNickNames) {
        return this.fileManagementService.uploadFile(file, userNickNames);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DELETE_FILE_ALIAS}, method = RequestMethod.GET)
    public String deleteFiles(@NotNull @PathVariable final String deleteHash) {
        return this.fileManagementService.deleteFiles(deleteHash);
    }

    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS + "{userName}"}, method = RequestMethod.GET)
    public FileSystemResource getProfilePic(@NotNull @PathVariable String userName) {
        return this.fileManagementService.sendProfilePicture(userName);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DOWNLOAD_FILE_ALIAS + "{downloadHash}"}, method = RequestMethod.GET)
    public FileSystemResource downloadFile(@NotNull @PathVariable String downloadHash) {
       return this.fileManagementService.downloadFile(downloadHash);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.GET_FILES_SHARED_WITH_ME_ALIAS}, method = RequestMethod.POST)
    public List<DataTransferObject> getSharedFiles(@NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                   @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
        return this.fileManagementService.getFilesISharedWithOtherUsers(firstResult, maxResults);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.GET_PRIVATE_FILES_ALIAS}, method = RequestMethod.POST)
    public List<DataTransferObject> getPrivateFiles(@NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                    @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
        return this.fileManagementService.getPrivateFiles(firstResult, maxResults);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.GET_UPLOADED_FILES_ALIAS}, method = RequestMethod.POST)
    public List<DataTransferObject> getUploadedFiles(@NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                     @NotNull @ModelAttribute("maxResults") final Integer maxResults) {
        return this.fileManagementService.getFilesSharedToMe(firstResult, maxResults);
    }
}
