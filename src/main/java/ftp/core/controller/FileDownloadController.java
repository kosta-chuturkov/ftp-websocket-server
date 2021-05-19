package ftp.core.controller;

import ftp.core.security.Authorities;
import ftp.core.service.face.FileManagementService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

@Controller
public class FileDownloadController {
    private final FileManagementService fileManagementService;

    @Autowired
    public FileDownloadController(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "downloadFile")
    @RequestMapping(path = "/api/v1/files/{downloadHash}/download/{fileName}", method = RequestMethod.GET)
    public void downloadFile(
            @NotNull @PathVariable String downloadHash,
            @NotNull @PathVariable String fileName,
            HttpServletResponse response) {
        this.fileManagementService.downloadFile(downloadHash, response);
//        return new ModelAndView();
    }
}
