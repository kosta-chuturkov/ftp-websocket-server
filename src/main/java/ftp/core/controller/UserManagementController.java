package ftp.core.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.model.entities.User;
import ftp.core.security.Authorities;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.impl.SchedulingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping(path = UserManagementController.PATH, produces = APPLICATION_JSON_VALUE)
@Api(tags = UserManagementController.TAG)
public class UserManagementController {

    public static final String TAG = "Users";

    public static final String PATH = "/api/v1/users";

    private UserService userService;

    private SchedulingService schedulingService;

    @Autowired
    public UserManagementController(UserService userService,
                                    SchedulingService schedulingService) {
        this.userService = userService;
        this.schedulingService = schedulingService;
    }

    @ApiOperation(value = "", nickname = "findUsers")
    @GetMapping()
    public DeferredResult<List<User>> findAllUsers() {
        return this.schedulingService
                .scheduleTask(() -> this.userService.findAll(),
                        10000L);
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "updateUsers")
    @PostMapping(value = "/{deleteHash}")
    public DeferredResult updateUsers(@NotNull @PathVariable final String deleteHash,
                                      @RequestBody final Set<ModifiedUserDto> modifiedUserDto) {
        return this.schedulingService
                .scheduleTask(() -> {
                    this.userService.updateUsers(deleteHash, modifiedUserDto);
                    return new DeferredResult();
                }, 10000L);
    }

}
