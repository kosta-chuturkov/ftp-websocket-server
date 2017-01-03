package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.security.Authorities;
import ftp.core.service.face.UserManagementService;
import ftp.core.service.impl.ReactorEventBusService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
public class UserManagementController {
    @Resource
    private UserManagementService userManagementService;

    @Resource
    private ReactorEventBusService reactorEventBusService;

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.QUERY_USERS_BY_NICK_NAME_ALIAS}, method = RequestMethod.POST)
    public DeferredResult<String> getUserInfo(@NotNull @ModelAttribute("q") final String userNickName) {
        return this.reactorEventBusService
                .scheduleTaskToReactor(() -> this.userManagementService.getUserDetails(userNickName), 10000L);
    }


    @Secured(Authorities.USER)
    @RequestMapping(value = {
            APIAliases.UPDATE_USERS_FILE_IS_SHARED_TO_ALIAS}, method = RequestMethod.POST)
    public DeferredResult updateUsers(@NotNull @PathVariable final String deleteHash, @RequestBody final Set<ModifiedUserDto> modifiedUserDto) {
        return this.reactorEventBusService
                .scheduleTaskToReactor(() -> {
                    this.userManagementService.updateUsers(deleteHash, modifiedUserDto);
                    return new DeferredResult();
                }, 10000L);
    }

}
