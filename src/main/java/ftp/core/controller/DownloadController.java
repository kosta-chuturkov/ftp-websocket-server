package ftp.core.controller;

import ftp.core.config.ServerConfigurator;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.model.entities.File;
import ftp.core.model.entities.File.FileType;
import ftp.core.model.entities.User;
import ftp.core.security.Authorities;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.ServerUtil;
import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class DownloadController {

    private static final Logger logger = Logger.getLogger(DownloadController.class);
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.DOWNLOAD_FILE_ALIAS + "*"}, method = RequestMethod.GET)
    public void downloadFile(final HttpServletRequest request, final HttpServletResponse response) {
        sendFile(request, response);
    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS + "{filename}"}, method = RequestMethod.GET)
    public void getProfilePic(final HttpServletResponse response, @PathVariable String filename) {
        filename += ".jpg";
        final java.io.File file = new java.io.File(ServerConfigurator.getProfilePicsFolder(), filename);
        ServerUtil.sendResourceByName(response, file.getAbsolutePath(), filename);
    }


    private void sendFile(final HttpServletRequest request, final HttpServletResponse response) {
        final User current = User.getCurrent();
        final String path = request.getServletPath();
        String downloadHash = "";
        if (path != null) {
            downloadHash = path.substring(APIAliases.DOWNLOAD_FILE_ALIAS.length(), path.length());
        }
        final String requesterEmail = current.getEmail();
        final String requesterNickName = current.getNickName();
        final File fileByDownloadHash = getFile(downloadHash);
        final Date timestamp = fileByDownloadHash.getTimestamp();
        final String fileName = fileByDownloadHash.getName();
        final FileType fileType = fileByDownloadHash.getFileType();
        String locationFolderName = "";
        if (this.fileService.isFileCreator(fileByDownloadHash.getId(), requesterNickName)) {
            locationFolderName = requesterEmail;
        } else {
            locationFolderName = getFolderNameByFileType(requesterNickName, fileByDownloadHash, fileType);
        }
        final String downloadPath = buildDownloadPath(timestamp, fileName, locationFolderName);
        ServerUtil.sendResourceByName(response, downloadPath, fileByDownloadHash.getName());
    }

    private File getFile(String downloadHash) {
        final File fileByDownloadHash = this.fileService.getFileByDownloadHash(downloadHash);
        if (fileByDownloadHash == null) {
            throw new RuntimeException("Unable to get requested file.");
        }
        return fileByDownloadHash;
    }

    private String buildDownloadPath(Date timestamp, String fileName, String locationFolderName) {
        return ServerConstants.SERVER_STORAGE_FOLDER_NAME.concat("/").concat(locationFolderName)
                .concat("/").concat(timestamp.getTime() + "_" + fileName);
    }

    private String getFolderNameByFileType(final String nickName, final File fileByDownloadHash, final FileType fileType) {
        String locationFolderName = "";
        switch (fileType) {
            case PRIVATE:
                throw new FtpServerException("You dont have permission to access this file.");
            case PUBLIC:
                locationFolderName = fileByDownloadHash.getCreator().getEmail();
                break;
            case SHARED:
                if (!this.fileService.isUserFromFileSharedUsers(fileByDownloadHash.getId(), nickName)) {
                    throw new FtpServerException(
                            "This file is not shared with you. You dont have permission to access this file.");
                }
                locationFolderName = fileByDownloadHash.getCreator().getEmail();
                break;
            default:
                break;
        }
        return locationFolderName;
    }

}
