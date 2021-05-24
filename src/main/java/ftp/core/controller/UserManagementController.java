package ftp.core.controller;

import ftp.core.model.dto.ModifiedUserDto;
import ftp.core.repository.projections.NickNameProjection;
import ftp.core.security.Authorities;
import ftp.core.service.face.tx.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = UserManagementController.PATH, produces = APPLICATION_JSON_VALUE)
@Api(tags = UserManagementController.TAG)
public class UserManagementController {

    public static final String TAG = "Users";

    public static final String PATH = "/api/v1/users";

    private UserService userService;


    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "findUsers")
    @GetMapping()
    public String findAllUsers() {
        return this.userService.getUserDetails();
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "getUserDetails")
    @GetMapping("/search")
    public List<NickNameProjection>  getUserInfo(
            @NotNull @ModelAttribute("q") final String userNickName) {
        return this.userService.getUserByNickLike(userNickName);
    }

    @Secured(Authorities.USER)
    @ApiOperation(value = "", nickname = "updateUsers")
    @PostMapping(value = "/{deleteHash}")
    public void updateUsers(@NotNull @PathVariable final String deleteHash,
                                      @RequestBody final Set<ModifiedUserDto> modifiedUserDto) {
        this.userService.updateUsers(deleteHash, modifiedUserDto);
    }

}
