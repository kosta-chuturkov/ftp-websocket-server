package ftp.core.controller;

import com.google.common.collect.Sets;
import com.google.gson.*;
import ftp.core.common.model.User;
import ftp.core.common.model.dto.JsonFileResponseDtoWrapper;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.config.ServerConfigurator;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import static ftp.core.common.util.ServerUtil.getProtocol;

@RestController
public class UploadController {

    private static final Logger logger = Logger.getLogger(UploadController.class);

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;

    @Resource
    private Gson gson;

    @Resource
    private JsonParser jsonParser;

    @RequestMapping(value = {"/upload**"}, method = RequestMethod.GET)
    public ModelAndView getLoginPage(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            if (ServerUtil.checkUserSession(request, true)) {
                return new ModelAndView(ServerConstants.UPLOAD_PAGE);
            } else {
                ServerUtil.invalidateSession(request, response);
                final ModelAndView modelAndView = new ModelAndView("redirect:" + ServerConstants.LOGIN_ALIAS);
                return modelAndView;
            }
        } catch (final Exception e) {
            throw new FtpServerException(e.getMessage());
        }
    }

    @RequestMapping(value = {"/profilePicUpdate**"}, method = RequestMethod.POST)
    public String profilePicUpdate(final HttpServletRequest request, final HttpServletResponse response,
                                   @RequestParam("files[]") final MultipartFile file) throws IOException {
        final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        final User current = this.userService.findByEmailAndPassword(email, password);
        JsonFileResponseDtoWrapper dtoWrapper = null;
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            if (!file.isEmpty()) {
                try {
                    final String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
                    final String extension = FilenameUtils.getExtension(fileName);
                    System.out.println(extension);
                    if (!ServerUtil.ALLOWED_EXTENTIONS.contains(extension)) {
                        throw new FtpServerException("Image expected...");
                    }
                    final int port = request.getServerPort();
                    final String host = request.getServerName();
                    final String serverFileName = current.getNickName() + "." + extension;
                    final String serverContextAddress = ServerUtil.getProtocol(request) + host + ":" + port
                            + ServerConstants.PROFILE_PIC_ALIAS + serverFileName;

                    final File imagesFolder = ServerConfigurator.getProfilePicsFolder();
                    final File targetFile = new File(imagesFolder, serverFileName);
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                    targetFile.createNewFile();
                    file.transferTo(targetFile);

                    Thumbnails.of(targetFile)
                            .size(50, 50)
                            .outputFormat("jpg")
                            .toFiles(Rename.NO_CHANGE);

                    final JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("imageUrl", serverContextAddress);
                    return jsonObject.toString();
                } catch (final Exception e) {
                    if (e instanceof HibernateException) {
                        dtoWrapper = new JsonFileResponseDtoWrapper.Builder().error("Unexpected error occured. Try again.").build();
                    } else {
                        dtoWrapper = new JsonFileResponseDtoWrapper.Builder().error(e.getMessage()).build();
                    }
                }
            } else {
                dtoWrapper = new JsonFileResponseDtoWrapper.Builder().error("You failed to upload " + file.getName() + " because the file was empty.").build();
            }
        }
        return geAstJsonObject(dtoWrapper).toString();
    }

    @RequestMapping(value = {"/upload**"}, method = RequestMethod.POST)
    public String uploadFile(final HttpServletRequest request, final HttpServletResponse response,
                             @RequestParam("files[]") final MultipartFile file, @RequestParam("modifier") final String modifier,
                             @RequestParam("nickName") final String userNickNames) throws IOException {
        final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        final User current = this.userService.findByEmailAndPassword(email, password);
        JsonFileResponseDtoWrapper dtoWrapper = null;
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            if (!file.isEmpty()) {
                try {
                    final int port = request.getServerPort();
                    final String host = request.getServerName();
                    final String contextPath = request.getContextPath();
                    final String serverContextAddress = getProtocol(request) + host + ":" + port + contextPath
                            + ServerConstants.FILES_ALIAS;
                    final Long token = current.getToken();
                    final String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
                    final long currentTime = System.currentTimeMillis();
                    final String tempFileName = new Long(currentTime).toString();
                    final String serverFileName = tempFileName + "_" + fileName;
                    final String deleteHash = ServerUtil.hash(ServerUtil.hash(serverFileName + token) + ServerConstants.DELETE_SALT);
                    final String downloadHash = ServerUtil.hash((serverFileName + token) + ServerConstants.DOWNLOAD_SALT);

                    final Set<String> users = getFileSharedUsersAsSet(userNickNames);
                    this.fileService.createFileRecord(fileName, currentTime, getModifier(modifier), users, file.getSize(),
                            deleteHash, downloadHash);
                    final File userFolder = getUserFolder(current.getEmail());
                    final File targetFile = new File(userFolder, serverFileName);
                    if (targetFile.exists()) {
                        targetFile.delete();
                    }
                    targetFile.createNewFile();

                    file.transferTo(targetFile);
                    dtoWrapper = new JsonFileResponseDtoWrapper.Builder().
                            name(StringEscapeUtils.escapeHtml(serverFileName)).
                            size(Long.toString(file.getSize())).url((serverContextAddress + downloadHash)).deleteUrl
                            ((serverContextAddress + ServerConstants.DELETE_ALIAS + deleteHash)).deleteType
                            ("GET").build();

                    final JSONObject parent = geAstJsonObject(dtoWrapper);
                    return parent.toString();
                } catch (final Exception e) {
                    if (e instanceof HibernateException) {
                        dtoWrapper = new JsonFileResponseDtoWrapper.Builder().error("Unexpected error occured. Try again.").build();
                    } else {
                        dtoWrapper = new JsonFileResponseDtoWrapper.Builder().error(e.getMessage()).build();
                    }
                }
            } else {
                dtoWrapper = new JsonFileResponseDtoWrapper.Builder().error("You failed to upload " + file.getName() + " because the file was empty.").build();
            }
        }
        return geAstJsonObject(dtoWrapper).toString();
    }

    private Set<String> getFileSharedUsersAsSet(final String userNickNames) {
        final Set<String> users = Sets.newHashSet();
        if (userNickNames.isEmpty()) return users;
        final JsonElement elem = this.jsonParser.parse(userNickNames);
        final JsonArray asJsonArray = elem.getAsJsonArray();
        for (final JsonElement jsonElement : asJsonArray) {
            final String name = jsonElement.getAsJsonObject().get("name").getAsString();
            users.add(name);
        }
        return users;
    }

    private JSONObject geAstJsonObject(final JsonFileResponseDtoWrapper dtoWrapper) {
        final JSONObject parent = new JSONObject();
        final JSONArray json = new JSONArray();
        final JSONObject jsonObject = new JSONObject(this.gson.toJson(dtoWrapper));
        json.put(jsonObject.get("abstractJsonResponceDto"));
        parent.put("files", json);
        return parent;
    }

    private int getModifier(final String modifierString) throws IOException {
        int modifier = -1;
        try {
            modifier = Integer.parseInt(modifierString);
            if (checkModifier(modifier)) {
                throw new FtpServerException(
                        "Modifier parameter is incorrect:" + modifierString + ".");
            }
        } catch (final NumberFormatException e) {
            throw new FtpServerException(
                    "Modifier parameter is incorrect:" + modifierString + ":" + ".The supported type is int.");
        }
        return modifier;
    }

    private boolean checkModifier(final int modifier) {
        return ftp.core.common.model.File.FileType.getById(modifier) == null;
    }

    private File getUserFolder(final String userName) {
        final File clientDir = new File(ServerConfigurator.getServerStorageFile(), userName);
        createUserDirIfNotExist(clientDir);
        return clientDir;
    }

    private void createUserDirIfNotExist(final File clientDir) {
        if (!clientDir.exists()) {
            clientDir.mkdir();
        }
    }


}
