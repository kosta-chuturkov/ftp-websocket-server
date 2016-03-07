package ftp.core.controller;

import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class DeleteController {

	private static final Logger logger = Logger.getLogger(DeleteController.class);
	@Resource
	private UserService userService;
	@Resource
	private FileService fileService;

	@RequestMapping(value = {
			ServerConstants.FILES_ALIAS + ServerConstants.DELETE_ALIAS + "*" }, method = RequestMethod.GET)
	public void deleteFiles(final HttpServletRequest request, final HttpServletResponse response) {
		try {
			final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
			final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
			final User current = this.userService.findByEmailAndPassword(email, password);
			if (current == null) {
				ServerUtil.sendJsonErrorResponce(response, "You must login first.");
			} else {
				User.setCurrent(current);
				deleteFile(request, response);
			}
		} catch (final Exception e) {
			logger.error("errror occured", e);
			ServerUtil.sendJsonErrorResponce(response, e.getMessage());
		}
	}

	private void deleteFile(final HttpServletRequest request, final HttpServletResponse response) {
		final String path = request.getServletPath();
		String deleteHash = "";
		if (path != null) {
			deleteHash = path.substring((ServerConstants.FILES_ALIAS + ServerConstants.DELETE_ALIAS).length(),
					path.length());
		}

		final User current = User.getCurrent();
		final String nickName = current.getNickName();
		final File findByDeleteHash = this.fileService.findByDeleteHash(deleteHash, nickName);
		if (findByDeleteHash == null) {
			ServerUtil.sendJsonErrorResponce(response, "File does not exist.");
		} else {
			final long fileSize = findByDeleteHash.getFileSize();
			final String name = findByDeleteHash.getName();
			final Date timestamp = findByDeleteHash.getTimestamp();
			this.fileService.delete(findByDeleteHash.getId());
			current.setRemainingStorage(current.getRemainingStorage() + fileSize);
			this.userService.update(current);
			final User updatedUser = (User) this.userService.findOne(current.getId());
			final String storageInfo = FileUtils.byteCountToDisplaySize(updatedUser.getRemainingStorage()) + " left from "
					+ FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT) + ".";
			ServerUtil.sendOkResponce(response, name, storageInfo);
			final String deletePath = ServerConstants.SERVER_STORAGE_FOLDER_NAME.concat("/").concat(updatedUser.getEmail())
					.concat("/").concat(timestamp.getTime() + "_" + name);
			final java.io.File fileToDelete = new java.io.File(deletePath);
			ServerUtil.deleteFile(fileToDelete);
		}
	}

}
