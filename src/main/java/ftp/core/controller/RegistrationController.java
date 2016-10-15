package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.ServerUtil;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Controller
public class RegistrationController {

    private static final Logger logger = Logger.getLogger(RegistrationController.class);
    @Resource
    private UserService userService;

    @RequestMapping(value = {APIAliases.REGISTRATION_ALIAS}, method = RequestMethod.GET)
    public ModelAndView getRegistrationPage(final HttpServletRequest request)
            throws IOException, ServletException {
        if (ServerUtil.userHasSession(request, true)) {
            final RedirectView view = new
                    RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
            view.setExposeModelAttributes(false);
            return new ModelAndView(view);
        } else {
            final ModelAndView modelAndView = new ModelAndView(ServerConstants.REGISTRATION_PAGE);
            return modelAndView;
        }

    }

    @RequestMapping(value = {APIAliases.REGISTRATION_ALIAS}, method = RequestMethod.POST)
    public ResponseEntity<?> register(final HttpServletRequest request, HttpServletResponse response, @NotNull @ModelAttribute("email") final String email,
                                      @NotNull @ModelAttribute("pswd") final String password, @NotNull @ModelAttribute("nickname") final String nickName,
                                      @NotNull @ModelAttribute("password_repeated") final String password_repeated) throws IOException, ServletException {
        if (ServerUtil.userHasSession(request, true)) {
            throw new IllegalArgumentException("Cannot register. You already have a session started.");
        }
        this.userService.validateUserCredentials(email, password, nickName, password_repeated);
        this.userService.registerUser(email, password, nickName, password_repeated);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

}
