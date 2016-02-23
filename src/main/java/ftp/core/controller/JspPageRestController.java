package ftp.core.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;

import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.model.dto.FileDto;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;

@RestController
public class JspPageRestController {

	private static final Logger logger = Logger.getLogger(JspPageRestController.class);
	@Resource
	private UserService userService;
	@Resource
	private FileService fileService;

	// @RequestMapping("/")
	// String home() {
	//
	// return "Hello World!";
	// }

	@RequestMapping(value = { "/files/shared/*" }, method = RequestMethod.POST)
	public List<FileDto> getSharedFiles(HttpServletRequest request, HttpServletResponse response,
			@NotNull @ModelAttribute("firstResult") Integer firstResult,
			@NotNull @ModelAttribute("maxResults") Integer maxResults) throws IOException {
		String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
		String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
		User current = userService.findByEmailAndPassword(email, password);
		List<FileDto> fileDtos = Lists.newArrayList();
		if (current == null) {
			ServerUtil.sendJsonErrorResponce(response, "You must login first.");
		} else {
			User.setCurrent(current);
			List<File> files = fileService.getSharedFilesForUser(current.getNickName(), firstResult, maxResults);
			for (File file : files) {
				FileDto fileDto = new FileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
						file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
				fileDtos.add(fileDto);
			}
		}
		return fileDtos;
	}

	@RequestMapping(value = {
			"/files/shared/d/*"
	}, method = RequestMethod.GET)
	public Object dasdas(HttpServletRequest request, HttpServletResponse response,
			@NotNull @ModelAttribute("firstResult") Integer firstResult,
			@NotNull @ModelAttribute("maxResults") Integer maxResults) throws IOException {

		throw new RuntimeException("dsadas");
	}

	@RequestMapping(value = { "/files/private/*" }, method = RequestMethod.POST)
	public List<FileDto> getPrivateFiles(HttpServletRequest request, HttpServletResponse response,
			@NotNull @ModelAttribute("firstResult") Integer firstResult,
			@NotNull @ModelAttribute("maxResults") Integer maxResults) throws IOException {
		String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
		String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
		User current = userService.findByEmailAndPassword(email, password);
		List<FileDto> fileDtos = Lists.newArrayList();
		if (current == null) {
			ServerUtil.sendJsonErrorResponce(response, "You must login first.");
		} else {
			User.setCurrent(current);
			List<File> files = fileService.getPrivateFilesForUser(current.getNickName(), firstResult, maxResults);
			for (File file : files) {
				FileDto fileDto = new FileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
						file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
				fileDtos.add(fileDto);
			}
		}
		return fileDtos;
	}
	
	@RequestMapping(value = { "/files/uploaded/*" }, method = RequestMethod.POST)
	public List<FileDto> getUploadedFiles(HttpServletRequest request, HttpServletResponse response,
			@NotNull @ModelAttribute("firstResult") Integer firstResult,
			@NotNull @ModelAttribute("maxResults") Integer maxResults) throws IOException {
		String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
		String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
		User current = userService.findByEmailAndPassword(email, password);
		List<FileDto> fileDtos = Lists.newArrayList();
		if (current == null) {
			ServerUtil.sendJsonErrorResponce(response, "You must login first.");
		} else {
			User.setCurrent(current);
			List<File> files = fileService.getUploadedFilesForUser(current.getNickName(), firstResult, maxResults);
			for (File file : files) {
				FileDto fileDto = new FileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
						file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
				fileDtos.add(fileDto);
			}
		}
		return fileDtos;
	}

	@RequestMapping(value = { "/usr*" }, method = RequestMethod.GET)
	public List<String> getUsersByNickName(HttpServletRequest request, HttpServletResponse response,
			@NotNull @ModelAttribute("q") String userNickName) throws IOException {
		String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
		String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
		User current = userService.findByEmailAndPassword(email, password);
		if (current == null) {
			ServerUtil.sendJsonErrorResponce(response, "You must login first.");
		} else {
			User.setCurrent(current);
			List<String> users = userService.getUserByNickLike(userNickName);
			return users;
		}
		return Lists.newArrayList();
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public final class ResourceNotFoundException extends RuntimeException {

		public ResourceNotFoundException() {
			super();
		}

		public ResourceNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}

		public ResourceNotFoundException(String message) {
			super(message);
		}

		public ResourceNotFoundException(Throwable cause) {
			super(cause);
		}
	}

}
