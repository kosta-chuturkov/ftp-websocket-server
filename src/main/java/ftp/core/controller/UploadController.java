package ftp.core.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;

import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.config.ServerConfigurator;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;

@Controller
public class UploadController {

	private static final Logger logger = Logger.getLogger(UploadController.class);
    @Resource
	private FileService fileService;
	@Resource
    private UserService userService;

    @RequestMapping(value = {"/upload**"}, method = RequestMethod.GET)
    public ModelAndView getLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (ServerUtil.checkUserSession(request, true)) {
                return new ModelAndView(ServerConstants.UPLOAD_PAGE);
            } else {
                ServerUtil.invalidateSession(request, response);
                ModelAndView modelAndView = new ModelAndView("redirect:" + ServerConstants.LOGIN_ALIAS);
                return modelAndView;
            }
        } catch (Exception e) {
            throw new FtpServerException(e.getMessage());
        }
    }

    @RequestMapping(value = {"/upload**"}, method = RequestMethod.POST)
	public void logIn(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("files[]") MultipartFile file, @RequestParam("modifier") String modifier,
			@RequestParam("nickName") String nickName) throws IOException {
        String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        User current = userService.findByEmailAndPassword(email, password);
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            if (!file.isEmpty()) {
                try {
					int port = request.getServerPort();
					String host = request.getServerName();
					String contextPath = request.getContextPath();
					String serverContextAddress = getProtocol(request) + host + ":" + port + contextPath
							+ ServerConstants.FILES_ALIAS;
                    BigDecimal token = current.getToken();
					String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
                    long currentTime = System.currentTimeMillis();
                    String tempFileName = new Long(currentTime).toString();
                    String serverFileName = tempFileName + "_" + fileName;
                    String deleteHash = ServerUtil.hash(ServerUtil.hash(serverFileName + token) + ServerConstants.DELETE_SALT);
                    String downloadHash = ServerUtil.hash((serverFileName + token) + ServerConstants.DOWNLOAD_SALT);
					fileService.createFileRecord(fileName, currentTime, getModifier(modifier), nickName, file.getSize(),
                            deleteHash, downloadHash);
                    File userFolder = getUserFolder(current.getEmail());
                    final File targetFile = new File(userFolder, serverFileName);
                    if (targetFile.exists()) {
                        targetFile.delete();
					}
                        targetFile.createNewFile();

                    file.transferTo(targetFile);
					Map<String, String> jsono = Maps.newHashMap();
					jsono.put("name", StringEscapeUtils.escapeHtml(serverFileName));
					jsono.put("size", Long.toString(file.getSize()));
					jsono.put("url", (serverContextAddress + downloadHash));
					jsono.put("thumbnail_url", "");
					jsono.put("deleteUrl", (serverContextAddress + ServerConstants.DELETE_ALIAS + deleteHash));
					jsono.put("deleteType", "GET");
					ServerUtil.sendPropertiesAsJson(response, jsono);
                } catch (Exception e) {
					if (e instanceof HibernateException) {
						ServerUtil.sendJsonErrorResponce(response, "Unexpected error occured. Try again.");
					} else {
						ServerUtil.sendJsonErrorResponce(response, e.getMessage());
					}
                }
            } else {
				ServerUtil.sendJsonErrorResponce(response,
						"You failed to upload " + file.getName() + " because the file was empty.");
            }
        }
    }

	private int getModifier(String modifierString) throws IOException {
        int modifier = -1;
        try {
			modifier = Integer.parseInt(modifierString);
            if (checkModifier(modifier)) {
                throw new FtpServerException(
					"Modifier parameter is incorrect:" + modifierString + ".");
            }
        } catch (NumberFormatException e) {
            throw new FtpServerException(
					"Modifier parameter is incorrect:" + modifierString + ":" + ".The supported type is int.");
        }
        return modifier;
    }

    private boolean checkModifier(int modifier) {
        return ftp.core.common.model.File.FileType.getById(modifier) == null;
    }

    private File getUserFolder(String userName) {
        File clientDir = new File(ServerConfigurator.getServerStorageFile(), userName);
        createUserDirIfNotExist(clientDir);
        return clientDir;
    }

    private void createUserDirIfNotExist(File clientDir) {
        if (!clientDir.exists()) {
            clientDir.mkdir();
        }
    }

    private String getProtocol(HttpServletRequest request) {
        boolean isSecure = request.isSecure();
        String protocol;
        if (isSecure) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }
        return protocol;
    }

}
