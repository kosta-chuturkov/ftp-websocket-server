package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.security.Authorities;
import ftp.core.service.face.UserManagementService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Set;

@RestController
public class UserManagementController {
    @Resource
    private UserManagementService userManagementService;

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.QUERY_USERS_BY_NICK_NAME_ALIAS}, method = RequestMethod.POST)
    public String getUserInfo(final HttpServletRequest request, final HttpServletResponse response,
                              @NotNull @ModelAttribute("q") final String userNickName) throws IOException {
        return this.userManagementService.getUserDetails(request, response, userNickName);
    }


    @Secured(Authorities.USER)
    @RequestMapping(value = {
            APIAliases.UPDATE_USERS_FILE_IS_SHARED_TO_ALIAS}, method = RequestMethod.POST)
    public void updateUsers(@NotNull @PathVariable final String deleteHash, @RequestBody final Set<ModifiedUserDto> modifiedUserDto) {
        this.userManagementService.updateUsers(deleteHash, modifiedUserDto);
    }

}
