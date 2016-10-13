package ftp.core.controller;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.model.dto.DataTransferObject;
import ftp.core.common.model.dto.ModifiedUserDto;
import ftp.core.common.model.dto.UploadedFileDto;
import ftp.core.common.util.ServerUtil;
import ftp.core.constants.APIAliases;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.impl.AuthenticationService;
import ftp.core.service.impl.EventService;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static ftp.core.common.util.ServerUtil.getAvatarUrl;

@RestController
public class JspPageRestController {

    private static final Logger logger = Logger.getLogger(JspPageRestController.class);
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private EventService eventService;
	@Resource
	private AuthenticationService authenticationService;


    @RequestMapping(value = {APIAliases.GET_FILES_SHARED_WITH_ME_ALIAS}, method = RequestMethod.POST)
    public List<DataTransferObject> getSharedFiles(final HttpServletRequest request, final HttpServletResponse response,
                                                   @NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                   @NotNull @ModelAttribute("maxResults") final Integer maxResults) throws IOException {

        final List<DataTransferObject> fileDtos = Lists.newArrayList();
		this.authenticationService.authenticateClient(request, response);
		final List<File> files = this.fileService.getSharedFilesForUser(User.getCurrent().getNickName(), firstResult,
				maxResults);
            for (final File file : files) {
                final DataTransferObject fileDto = new UploadedFileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                        file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
                fileDtos.add(fileDto);

        }
        return fileDtos;
    }


    @RequestMapping(value = {APIAliases.GET_PRIVATE_FILES_ALIAS}, method = RequestMethod.POST)
    public List<DataTransferObject> getPrivateFiles(final HttpServletRequest request, final HttpServletResponse response,
                                                    @NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                    @NotNull @ModelAttribute("maxResults") final Integer maxResults) throws IOException {
        final List<DataTransferObject> fileDtos = Lists.newArrayList();
		this.authenticationService.authenticateClient(request, response);
		final List<File> files = this.fileService.getPrivateFilesForUser(User.getCurrent().getNickName(), firstResult,
				maxResults);
            for (final File file : files) {
                final DataTransferObject fileDto = new UploadedFileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                        file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
                fileDtos.add(fileDto);
            }

        return fileDtos;
    }

    @RequestMapping(value = {APIAliases.GET_UPLOADED_FILES_ALIAS}, method = RequestMethod.POST)
    public List<DataTransferObject> getUploadedFiles(final HttpServletRequest request, final HttpServletResponse response,
                                                     @NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                                     @NotNull @ModelAttribute("maxResults") final Integer maxResults) throws IOException {
        final List<DataTransferObject> fileDtos = Lists.newArrayList();
		this.authenticationService.authenticateClient(request, response);
		final List<Long> files = this.fileService.getSharedFilesWithUsersIds(User.getCurrent().getId(), firstResult,
				maxResults);
            for (final Long fileId : files) {
                final File file = this.fileService.findWithSharedUsers(fileId);
                final DataTransferObject fileDto = new UploadedFileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                        file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
                fileDtos.add(fileDto);
            }
        return fileDtos;
    }

	@RequestMapping(value = { APIAliases.QUERY_USERS_BY_NICK_NAME_ALIAS }, method = RequestMethod.POST)
    public String usrs(final HttpServletRequest request, final HttpServletResponse response,
                       @NotNull @ModelAttribute("q") final String userNickName) throws IOException {
		this.authenticationService.authenticateClient(request, response);
        final int port = request.getServerPort();
        final String host = request.getServerName();
        final String serverContextAddress = ServerUtil.getProtocol(request) + host + ":" + port;
        final String profilePicAddress = serverContextAddress + APIAliases.PROFILE_PIC_ALIAS;
        final JsonObject jsonResponse = new JsonObject();

            final List<String> users = this.userService.getUserByNickLike(userNickName);

            final JsonArray jsonArrayWrapper = new JsonArray();
            for (final String user : users) {
                final JsonObject userObject = new JsonObject();
                userObject.addProperty("id", user);
                userObject.addProperty("full_name", user);
                final JsonObject owner = new JsonObject();
                owner.addProperty("id", Math.random());
                owner.addProperty("avatar_url", getAvatarUrl(serverContextAddress, profilePicAddress, user));
                userObject.add("owner", owner);
                jsonArrayWrapper.add(userObject);
            }
            jsonResponse.addProperty("total_count", users.size());
            jsonResponse.addProperty("incomplete_results", Boolean.FALSE);
            jsonResponse.add("items", jsonArrayWrapper);

        return jsonResponse.toString();
    }


    @RequestMapping(value = {
            APIAliases.UPDATE_USERS_FILE_IS_SHARED_TO_ALIAS}, method = RequestMethod.POST, consumes = "application/json")
    public void updateUsers(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String deleteHash, @RequestBody final Set<ModifiedUserDto> modifiedUserDto) {
        try {
			this.authenticationService.authenticateClient(request, response);
			this.fileService.updateUsers(deleteHash, modifiedUserDto);
        } catch (final Exception e) {
            logger.error("errror occured", e);
            ServerUtil.sendJsonErrorResponce(response, e.getMessage());
        }
    }


    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public final class ResourceNotFoundException extends RuntimeException {

        public ResourceNotFoundException() {
            super();
        }

        public ResourceNotFoundException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public ResourceNotFoundException(final String message) {
            super(message);
        }

        public ResourceNotFoundException(final Throwable cause) {
            super(cause);
        }
    }

}
