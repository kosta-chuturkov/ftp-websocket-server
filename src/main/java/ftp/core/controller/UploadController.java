package ftp.core.controller;

import com.google.common.collect.Sets;
import com.google.gson.*;
import ftp.core.common.model.User;
import ftp.core.common.model.dto.JsonErrorDto;
import ftp.core.common.model.dto.JsonFileDto;
import ftp.core.common.model.dto.ResponseModelAdapter;
import ftp.core.common.util.HttpRequestParameters;
import ftp.core.common.util.ServerUtil;
import ftp.core.config.ServerConfigurator;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.security.Authorities;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.json.JSONObject;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Supplier;

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


    @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS}, method = RequestMethod.GET)
    public ModelAndView getUploadPage(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        if (ServerUtil.userHasSession(request, true)) {
            return new ModelAndView(ServerConstants.UPLOAD_PAGE);
        } else {
            final ModelAndView modelAndView = new ModelAndView("redirect:" + APIAliases.LOGIN_ALIAS);
            return modelAndView;
        }

    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.PROFILE_PIC_ALIAS}, method = RequestMethod.POST)
    public String profilePicUpdate(final HttpServletRequest request, final HttpServletResponse response,
                                   @RequestParam("files[]") final MultipartFile file) throws IOException {

        final String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
        final String extension = FilenameUtils.getExtension(fileName);
        checkFileExtention(extension);
        final String serverFileName = User.getCurrent().getNickName() + "." + extension;
        final String imageUrlAddress = buildImageUrlAddress(request, serverFileName);

        final File targetFile = createFileInFolder(serverFileName, ServerConfigurator.getProfilePicsFolder());
        transferToTargetFile(file, targetFile);

        createImageThumbnail(targetFile, 50, 50);
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imageUrl", imageUrlAddress);
        return jsonObject.toString();


    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS}, method = RequestMethod.POST)
    public String uploadFile(final HttpServletRequest request, final HttpServletResponse response,
                             @RequestParam("files[]") final MultipartFile file, @RequestParam("modifier") final String modifier,
                             @RequestParam("nickName") final String userNickNames) throws IOException {

        final int port = request.getServerPort();
        final String host = request.getServerName();
        final String contextPath = request.getContextPath();
        final String serverContextAddress = getProtocol(request) + host + ":" + port + contextPath
                + APIAliases.DOWNLOAD_FILE_ALIAS;
        final Long token = User.getCurrent().getToken();
        final String fileName = StringEscapeUtils.escapeSql(file.getOriginalFilename());
        final long currentTime = System.currentTimeMillis();
        final String tempFileName = new Long(currentTime).toString();
        final String serverFileName = tempFileName + "_" + fileName;
        final String deleteHash = ServerUtil.hash(ServerUtil.hash(serverFileName + token) + ServerConstants.DELETE_SALT);
        final String downloadHash = ServerUtil.hash((serverFileName + token) + ServerConstants.DOWNLOAD_SALT);

        final Set<String> users = getFileSharedUsersAsSet(userNickNames);
        this.fileService.createFileRecord(fileName, currentTime, getModifier(modifier), users, file.getSize(),
                deleteHash, downloadHash);
        final File userFolder = getUserFolder(User.getCurrent().getEmail());
        final File targetFile = createFileInFolder(serverFileName, userFolder);
        transferToTargetFile(file, targetFile);
        return buildResponseObject(file, serverContextAddress, fileName, deleteHash, downloadHash).toString();

    }

    private String buildErrorResponse(String errorMessage) {
        return buildErrorResponse(errorMessage, new JsonErrorDto());
    }

    private String buildErrorResponse(String errorMessage, JsonErrorDto jsonErrorDto) {
        jsonErrorDto.setError(errorMessage);
        return ServerUtil.geAstJsonObject(new ResponseModelAdapter.Builder().withBaseFileDto(jsonErrorDto).build()).toString();
    }

    private void transferToTargetFile(@RequestParam("files[]") MultipartFile file, File targetFile) throws IOException {
        file.transferTo(targetFile);
    }

    private String buildImageUrlAddress(HttpServletRequest request, String serverFileName) {
        final int port = request.getServerPort();
        final String host = request.getServerName();
        return ServerUtil.getProtocol(request) + host + ":" + port
                + APIAliases.PROFILE_PIC_ALIAS + serverFileName;
    }

    private void createImageThumbnail(File targetFile, int width, int height) throws IOException {
        Thumbnails.of(targetFile)
                .size(width, height)
                .outputFormat("jpg")
                .toFiles(Rename.NO_CHANGE);
    }

    private void checkFileExtention(String extension) {
        if (!ServerUtil.ALLOWED_EXTENTIONS.contains(extension)) {
            throw new FtpServerException("Image expected...");
        }
    }


    private String handleRequest(HttpRequestParameters parameters, Supplier<String> supplier) {
        JsonErrorDto jsonErrorDto = null;
        String errorMessage;
        MultipartFile file = parameters.getFile();
        if (file.isEmpty()) {
            return buildErrorResponse("You failed to upload " + file.getName() + " because the file was empty.");
        }
        try {
            return supplier.get();
        } catch (final Exception e) {
            if (e instanceof HibernateException) {
                errorMessage = "Unexpected error occured. Try again.";
            } else {
                errorMessage = e.getMessage();
            }
        }
        return buildErrorResponse(errorMessage, jsonErrorDto);
    }

    private JSONObject buildResponseObject(@RequestParam("files[]") MultipartFile file, String serverContextAddress, String fileName, String deleteHash, String downloadHash) {
        JsonFileDto dtoWrapper = new JsonFileDto.Builder()
                .withName(StringEscapeUtils.escapeHtml(fileName))
                .withSize(Long.toString(file.getSize()))
                .withUrl((serverContextAddress + downloadHash))
                .withDeleteUrl((serverContextAddress + ServerConstants.DELETE_ALIAS + deleteHash))
                .withDeleteType("GET")
                .build();

        return ServerUtil.geAstJsonObject(new ResponseModelAdapter.Builder().withBaseFileDto(dtoWrapper).build());
    }

    private File createFileInFolder(String serverFileName, File userFolder) throws IOException {
        final File targetFile = new File(userFolder, serverFileName);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        targetFile.createNewFile();
        return targetFile;
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
