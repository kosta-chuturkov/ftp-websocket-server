package ftp.core.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;

@Controller
public class DeleteController {

	@Resource
	private UserService userService;

	@Resource
	private FileService fileService;
	
	private static final Logger logger = Logger.getLogger(DeleteController.class);

	@RequestMapping(value = {
			ServerConstants.FILES_ALIAS + ServerConstants.DELETE_ALIAS + "*" }, method = RequestMethod.GET)
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
		try {
			String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
			String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
			User current = userService.findByEmailAndPassword(email, password);
			if (current == null) {
				ServerUtil.sendJsonErrorResponce(response, "You must login first.");
			} else {
				User.setCurrent(current);
				deleteFile(request, response);
			}
		} catch (Exception e) {
			logger.error("errror occured", e);
			ServerUtil.sendJsonErrorResponce(response, e.getMessage());
		}
	}

	private void deleteFile(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getServletPath();
		String deleteHash = "";
		if (path != null) {
			deleteHash = path.substring((ServerConstants.FILES_ALIAS + ServerConstants.DELETE_ALIAS).length(),
					path.length());
		}

		User current = User.getCurrent();
		String nickName = current.getNickName();
		File findByDeleteHash = fileService.findByDeleteHash(deleteHash, nickName);
		if (findByDeleteHash == null) {
			ServerUtil.sendJsonErrorResponce(response, "File does not exist.");
		} else {
			long fileSize = findByDeleteHash.getFileSize();
			String name = findByDeleteHash.getName();
			Date timestamp = findByDeleteHash.getTimestamp();
			fileService.delete(findByDeleteHash);
			current.setRemainingStorage(current.getRemainingStorage() + fileSize);
			userService.update(current);
			User updatedUser = (User) userService.findOne(current.getId());
			String storageInfo = FileUtils.byteCountToDisplaySize(updatedUser.getRemainingStorage()) + " left from "
					+ FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT) + ".";
			ServerUtil.sendOkResponce(response, name, storageInfo);
			String deletePath = ServerConstants.SERVER_STORAGE_FOLDER_NAME.concat("/").concat(updatedUser.getEmail())
					.concat("/").concat(timestamp.getTime() + "_" + name);
			java.io.File fileToDelete = new java.io.File(deletePath);
			ServerUtil.deleteFile(fileToDelete);
		}
	}

}
