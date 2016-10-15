package ftp.core.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ftp.core.common.model.dto.ModifiedUserDto;
import ftp.core.common.util.ServerUtil;
import ftp.core.constants.APIAliases;
import ftp.core.security.Authorities;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.impl.EventService;
import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
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

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.QUERY_USERS_BY_NICK_NAME_ALIAS}, method = RequestMethod.POST)
    public String usrs(final HttpServletRequest request, final HttpServletResponse response,
                       @NotNull @ModelAttribute("q") final String userNickName) throws IOException {
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


    @Secured(Authorities.USER)
    @RequestMapping(value = {
            APIAliases.UPDATE_USERS_FILE_IS_SHARED_TO_ALIAS}, method = RequestMethod.POST, consumes = "application/json")
    public void updateUsers(final HttpServletRequest request, final HttpServletResponse response, @PathVariable final String deleteHash, @RequestBody final Set<ModifiedUserDto> modifiedUserDto) {
        this.fileService.updateUsers(deleteHash, modifiedUserDto);
    }

}
