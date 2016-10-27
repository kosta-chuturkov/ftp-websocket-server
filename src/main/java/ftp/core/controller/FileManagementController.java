package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController("fileManagementController")
public class FileManagementController {

    @Resource
    private FileManagementService fileManagementService;

    @Autowired
    private ServletContext servletContext;


    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS}, method = RequestMethod.POST)
    public String updateProfilePicture(final HttpServletRequest request,
                                       @RequestParam("files[]") final MultipartFile file) {
        return this.fileManagementService.updateProfilePicture(request, file);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS + "*"}, method = RequestMethod.POST)
    public String uploadFile(final HttpServletRequest request,
                             @RequestParam("files[]") final MultipartFile file,
                             @RequestParam("nickName") final String userNickNames) {
        return this.fileManagementService.uploadFile(request, file, userNickNames);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DELETE_FILE_ALIAS}, method = RequestMethod.GET)
    public void deleteFiles(final HttpServletResponse response, @NotNull @PathVariable final String deleteHash) {
        this.fileManagementService.deleteFiles(response, deleteHash);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DOWNLOAD_FILE_ALIAS + "{downloadHash}"}, method = RequestMethod.GET)
    public void downloadFile(@NotNull @PathVariable String downloadHash, final HttpServletResponse response) {
        this.fileManagementService.downloadFile(downloadHash, response);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS + "{userName}"}, method = RequestMethod.GET)
    public void getProfilePic(final HttpServletResponse response, @NotNull @PathVariable String userName) {
        this.fileManagementService.sendProfilePicture(response, userName);
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
