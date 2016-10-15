package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class FileManagementController {

    @Resource
    private FileManagementService fileManagementService;

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS}, method = RequestMethod.POST)
    public String profilePicUpdate(final HttpServletRequest request,
                                   @RequestParam("files[]") final MultipartFile file) throws IOException {
        return fileManagementService.updateProfilePicture(request, file);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS}, method = RequestMethod.POST)
    public String uploadFile(final HttpServletRequest request,
                             @RequestParam("files[]") final MultipartFile file, @RequestParam("modifier") final String modifier,
                             @RequestParam("nickName") final String userNickNames) throws IOException {

        return fileManagementService.uploadFile(request, file, modifier, userNickNames);
    }
}
