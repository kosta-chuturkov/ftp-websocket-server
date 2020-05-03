package ftp.core.controller;

import ftp.core.model.dto.RegistrationRequest;
import ftp.core.model.entities.User;
import ftp.core.service.face.tx.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PageNavigationController {

    public static final String PATH = "/api/v1/register";

    @Resource
    private UserService userService;

    @PostMapping(value = PATH)
    public ResponseEntity<?> registerUser(@NotNull @RequestBody RegistrationRequest registrationRequest) {
        if (User.isAuthenticated()) {
            throw new IllegalArgumentException("Cannot register. You already have a session started.");
        }
        this.userService.registerUser(registrationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}

