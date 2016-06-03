package ftp.core.controller;

import ftp.core.common.model.File;
import ftp.core.common.model.File.FileType;
import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.config.ServerConfigurator;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class DownloadController {

    private static final Logger logger = Logger.getLogger(DownloadController.class);
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;

    @RequestMapping(value = {ServerConstants.FILES_ALIAS + "*"}, method = RequestMethod.GET)
    public ModelAndView downloadFile(final HttpServletRequest request, final HttpServletResponse response) {

        try {
            final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
            final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
            final User current = this.userService.findByEmailAndPassword(email, password);
            if (current == null) {
                ServerUtil.sendJsonErrorResponce(response, "You must login first.");
            } else {
                User.setCurrent(current);
                sendFile(request, response);
            }
        } catch (final Exception e) {
            logger.error("error occured", e);
            return new ModelAndView(ServerConstants.RESOURCE_NOT_FOUND_PAGE).addObject("errorMsg", e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = {ServerConstants.PROFILE_PIC_ALIAS + "{filename}"}, method = RequestMethod.GET)
    public ModelAndView getProfilePic(final HttpServletResponse response, @PathVariable String filename) {

        try {
            filename += ".jpg";
            final java.io.File file = new java.io.File(ServerConfigurator.getProfilePicsFolder(), filename);
            ServerUtil.sendResourceByName(response, file.getAbsolutePath(), filename);
        } catch (final Exception e) {
            logger.error("error occured", e);
            return new ModelAndView(ServerConstants.RESOURCE_NOT_FOUND_PAGE).addObject("errorMsg", e.getMessage());
        }
        return null;
    }


    private void sendFile(final HttpServletRequest request, final HttpServletResponse response) {
        final String path = request.getServletPath();
        String downloadHash = "";
        if (path != null) {
            downloadHash = path.substring(ServerConstants.FILES_ALIAS.length(), path.length());
        }
        final User current = User.getCurrent();
        final String requesterEmail = current.getEmail();
        final String requesterNickName = current.getNickName();
        final File fileByDownloadHash = this.fileService.getFileByDownloadHash(downloadHash);
        if (fileByDownloadHash == null) {
            ServerUtil.sendJsonErrorResponce(response, "Unable to get requested file.");
        } else {
            final Date timestamp = fileByDownloadHash.getTimestamp();
            final String fileName = fileByDownloadHash.getName();
            final FileType fileType = fileByDownloadHash.getFileType();
            String locationFolderName = "";
            if (this.fileService.isFileCreator(fileByDownloadHash.getId(), requesterNickName)) {
                locationFolderName = requesterEmail;
            } else {
                locationFolderName = getFolderNameByFileType(requesterNickName, fileByDownloadHash, fileType);
            }
            final String downloadPath = ServerConstants.SERVER_STORAGE_FOLDER_NAME.concat("/").concat(locationFolderName)
                    .concat("/").concat(timestamp.getTime() + "_" + fileName);
            ServerUtil.sendResourceByName(response, downloadPath, fileByDownloadHash.getName());
        }
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
