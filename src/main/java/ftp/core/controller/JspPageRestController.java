package ftp.core.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ftp.core.common.model.File;
import ftp.core.common.model.User;
import ftp.core.common.model.dto.FileDto;
import ftp.core.common.model.dto.MainPageFileDto;
import ftp.core.common.model.dto.ModifiedUsersDto;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import org.apache.commons.lang.StringEscapeUtils;
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

@RestController
public class JspPageRestController {

    private static final Logger logger = Logger.getLogger(JspPageRestController.class);
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;

    @RequestMapping(value = {"/files/shared/*"}, method = RequestMethod.POST)
    public List<FileDto> getSharedFiles(final HttpServletRequest request, final HttpServletResponse response,
                                        @NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                        @NotNull @ModelAttribute("maxResults") final Integer maxResults) throws IOException {
        final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        final User current = this.userService.findByEmailAndPassword(email, password);
        final List<FileDto> fileDtos = Lists.newArrayList();
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            final List<File> files = this.fileService.getSharedFilesForUser(current.getNickName(), firstResult, maxResults);
            for (final File file : files) {
                final FileDto fileDto = new MainPageFileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                        file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }


    @RequestMapping(value = {"/files/private/*"}, method = RequestMethod.POST)
    public List<FileDto> getPrivateFiles(final HttpServletRequest request, final HttpServletResponse response,
                                         @NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                         @NotNull @ModelAttribute("maxResults") final Integer maxResults) throws IOException {
        final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        final User current = this.userService.findByEmailAndPassword(email, password);
        final List<FileDto> fileDtos = Lists.newArrayList();
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            final List<File> files = this.fileService.getPrivateFilesForUser(current.getNickName(), firstResult, maxResults);
            for (final File file : files) {
                final FileDto fileDto = new MainPageFileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                        file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }

    @RequestMapping(value = {"/files/uploaded/*"}, method = RequestMethod.POST)
    public List<FileDto> getUploadedFiles(final HttpServletRequest request, final HttpServletResponse response,
                                          @NotNull @ModelAttribute("firstResult") final Integer firstResult,
                                          @NotNull @ModelAttribute("maxResults") final Integer maxResults) throws IOException {
        final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        final User current = this.userService.findByEmailAndPassword(email, password);
        final List<FileDto> fileDtos = Lists.newArrayList();
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            final List<Long> files = this.fileService.getSharedFilesWithUsersIds(current.getId(), firstResult, maxResults);
            for (final Long fileId : files) {
                final File file = this.fileService.findWithSharedUsers(fileId);
                final FileDto fileDto = new MainPageFileDto(file.getCreator().getNickName(), file.getName(), file.getDownloadHash(),
                        file.getDeleteHash(), file.getFileSize(), file.getTimestamp().toString(), file.getFileType());
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }

    @RequestMapping(value = {"/usr*"}, method = RequestMethod.GET)
    public List<String> getUsersByNickName(final HttpServletRequest request, final HttpServletResponse response,
                                           @NotNull @ModelAttribute("q") final String userNickName) throws IOException {
        final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        final User current = this.userService.findByEmailAndPassword(email, password);
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            final List<String> users = this.userService.getUserByNickLike(userNickName);
            return users;
        }
        return Lists.newArrayList();
    }

    @RequestMapping(value = {"/users*"}, method = RequestMethod.GET)
    public String usrs(final HttpServletRequest request, final HttpServletResponse response,
                       @NotNull @ModelAttribute("q") final String userNickName) throws IOException {
        final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
        final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
        final User current = this.userService.findByEmailAndPassword(email, password);
        final JsonObject jsonResponse = new JsonObject();
        if (current == null) {
            ServerUtil.sendJsonErrorResponce(response, "You must login first.");
        } else {
            User.setCurrent(current);
            final List<String> users = this.userService.getUserByNickLike(userNickName);

            final JsonArray jsonArrayWrapper = new JsonArray();
            for (final String user : users) {
                final JsonObject userObject = new JsonObject();
                userObject.addProperty("id", user);
                userObject.addProperty("full_name", user);
                jsonArrayWrapper.add(userObject);
            }
            jsonResponse.addProperty("total_count", users.size());
            jsonResponse.addProperty("incomplete_results", Boolean.FALSE);
            jsonResponse.add("items", jsonArrayWrapper);
        }
        return jsonResponse.toString();
    }

    @RequestMapping(value = {
            ServerConstants.FILES_ALIAS + ServerConstants.UPDATE_ALIAS + "/{fileHash}"}, method = RequestMethod.POST, consumes = "application/json")
    public void updateUsers(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String fileHash, @RequestBody final Set<ModifiedUsersDto> modifiedUsersDto) {
        try {
            final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
            final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
            final User current = this.userService.findByEmailAndPassword(email, password);
            if (current == null) {
                ServerUtil.sendJsonErrorResponce(response, "You must login first.");
            } else {
                User.setCurrent(current);
                updateUsers0(fileHash, modifiedUsersDto);
            }
        } catch (final Exception e) {
            logger.error("errror occured", e);
            ServerUtil.sendJsonErrorResponce(response, e.getMessage());
        }
    }

    private void updateUsers0(final String fileHash, final Set<ModifiedUsersDto> modifiedUsersDto) {
        final Set<String> userNickNames = Sets.newHashSet();
        if (modifiedUsersDto.size() == 1 && modifiedUsersDto.iterator().next().getName() == null) {
            this.fileService.updateUsersForFile(fileHash, userNickNames);
        } else {
            for (final ModifiedUsersDto usersDto : modifiedUsersDto) {
                final String name = usersDto.getName();
                final String escapedUserName = StringEscapeUtils.escapeSql(name);
                final User userByNickName = this.userService.getUserByNickName(escapedUserName);
                if (userByNickName == null) {
                    throw new FtpServerException("Wrong parameters");
                }
                if (escapedUserName.equals(User.getCurrent().getNickName())) {
                    throw new FtpServerException("Cant share file with yourself.");
                }
                userNickNames.add(escapedUserName);
            }

            this.fileService.updateUsersForFile(fileHash, userNickNames);
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
