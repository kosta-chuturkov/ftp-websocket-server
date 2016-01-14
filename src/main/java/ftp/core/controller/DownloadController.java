package ftp.core.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ftp.core.common.model.File;
import ftp.core.common.model.File.FileType;
import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;

@Controller
public class DownloadController {

	@Resource
	private UserService userService;

	@Resource
	private FileService fileService;

	private static final Logger logger = Logger.getLogger(DownloadController.class);

	@RequestMapping(value = { ServerConstants.FILES_ALIAS + "*" }, method = RequestMethod.GET)
	public ModelAndView downloadFile(HttpServletRequest request, HttpServletResponse response) {

		try {
			String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
			String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
			User current = userService.findByEmailAndPassword(email, password);
			if (current == null) {
				ServerUtil.sendJsonErrorResponce(response, "You must login first.");
			} else {
				User.setCurrent(current);
				sendFile(request, response);
			}
		} catch (Exception e) {
			logger.error("error occured", e);
			return new ModelAndView(ServerConstants.RESOURCE_NOT_FOUND_PAGE).addObject("errorMsg", e.getMessage());
		}
		return null;
	}

	private void sendFile(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getServletPath();
		String downloadHash = "";
		if (path != null) {
			downloadHash = path.substring(ServerConstants.FILES_ALIAS.length(), path.length());
		}
		User current = User.getCurrent();
		String requesterEmail = current.getEmail();
		String requesterNickName = current.getNickName();
		File fileByDownloadHash = fileService.getFileByDownloadHash(downloadHash);
		if (fileByDownloadHash == null) {
			ServerUtil.sendJsonErrorResponce(response, "Unable to get requested file.");
		} else {
			Date timestamp = fileByDownloadHash.getTimestamp();
			String fileName = fileByDownloadHash.getName();
			FileType fileType = fileByDownloadHash.getFileType();
			String locationFolderName = "";
			if (fileService.isFileCreator(fileByDownloadHash.getId(), requesterNickName)) {
				locationFolderName = requesterEmail;
			} else {
				locationFolderName = getFolderNameByFileType(requesterNickName, fileByDownloadHash, fileType);
			}
			String downloadPath = ServerConstants.SERVER_STORAGE_FOLDER_NAME.concat("/").concat(locationFolderName)
					.concat("/").concat(timestamp.getTime() + "_" + fileName);
			ServerUtil.sendResourceByName(response, downloadPath, fileByDownloadHash.getName());
		}
	}

	private void printHeaderNames(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String nextElement = headerNames.nextElement();
			System.out.println(nextElement + ":" + request.getHeader(nextElement));
		}
	}

	private String getFolderNameByFileType(String nickName, File fileByDownloadHash, FileType fileType) {
		String locationFolderName = "";
		switch (fileType) {
		case PRIVATE:
			throw new FtpServerException("You dont have permission to access this file.");
		case PUBLIC:
			locationFolderName = fileByDownloadHash.getCreator().getEmail();
			break;
		case SHARED:
			if (!fileService.isUserFromFileSharedUsers(fileByDownloadHash.getId(), nickName)) {
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
