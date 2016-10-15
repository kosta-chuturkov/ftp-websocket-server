package ftp.core.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ftp.core.constants.APIAliases;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.security.Authorities;
import ftp.core.service.face.UserManagementService;
import ftp.core.service.face.tx.FileService;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.ServerUtil;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static ftp.core.util.ServerUtil.getAvatarUrl;

@Service("userManagementService")
public class UserManagementServiceImpl implements UserManagementService {
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private EventService eventService;

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.QUERY_USERS_BY_NICK_NAME_ALIAS}, method = RequestMethod.POST)
    public String getUserInfo(final HttpServletRequest request, final HttpServletResponse response,
                              final String userNickName) throws IOException {
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
            APIAliases.UPDATE_USERS_FILE_IS_SHARED_TO_ALIAS}, method = RequestMethod.POST)
    public void updateUsers(final String deleteHash, final Set<ModifiedUserDto> modifiedUserDto) {
        this.fileService.updateUsers(deleteHash, modifiedUserDto);
    }
}
