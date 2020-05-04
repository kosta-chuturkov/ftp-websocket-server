package ftp.core.controller;

import ftp.core.model.dto.RegistrationRequest;
import ftp.core.model.entities.User;
import ftp.core.service.face.tx.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = UserRegistrationController.PATH)
public class UserRegistrationController {

    public static final String PATH = "/api/v1/register";

    @Resource
    private UserService userService;

    @PostMapping
    public void registerUser(@NotNull @RequestBody RegistrationRequest registrationRequest) {
        if (User.isAuthenticated()) {
            throw new IllegalArgumentException("Cannot register. You already have a session started.");
        }
        this.userService.registerUser(registrationRequest);
    }

}

