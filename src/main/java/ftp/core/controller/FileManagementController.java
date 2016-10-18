package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class FileManagementController {

    @Resource
    private FileManagementService fileManagementService;

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS}, method = RequestMethod.POST)
    public String profilePicUpdate(final HttpServletRequest request,
                                   @RequestParam("files[]") final MultipartFile file) throws IOException {
        return this.fileManagementService.updateProfilePicture(request, file);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS}, method = RequestMethod.POST)
    public String uploadFile(final HttpServletRequest request,
                             @RequestParam("files[]") final MultipartFile file, @RequestParam("modifier") final String modifier,
                             @RequestParam("nickName") final String userNickNames) throws IOException {
        return this.fileManagementService.uploadFile(request, file, modifier, userNickNames);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DELETE_FILE_ALIAS}, method = RequestMethod.GET)
    public void deleteFiles(final HttpServletResponse response, @PathVariable final String deleteHash) {
        this.fileManagementService.deleteFiles(response, deleteHash);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DOWNLOAD_FILE_ALIAS + "{downloadHash}"}, method = RequestMethod.GET)
    public void downloadFile(@PathVariable String downloadHash, final HttpServletResponse response) {
        this.fileManagementService.downloadFile(downloadHash, response);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS + "{userName}"}, method = RequestMethod.GET)
    public void getProfilePic(final HttpServletResponse response, @PathVariable String userName) {
        this.fileManagementService.sendProfilePicture(response, userName);
    }
}
