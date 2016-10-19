package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.security.Authorities;
import ftp.core.security.SecurityUtils;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.ServerUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
public class PageNavigationController {

    @Resource
    private UserService userService;

    @RequestMapping(value = {APIAliases.REGISTRATION_ALIAS}, method = RequestMethod.POST)
    public ResponseEntity<?> registerUser(@NotNull @ModelAttribute("email") final String email,
                                          @NotNull @ModelAttribute("pswd") final String password, @NotNull @ModelAttribute("nickname") final String nickName,
                                          @NotNull @ModelAttribute("password_repeated") final String password_repeated) throws IOException, ServletException {
        if (SecurityUtils.isAuthenticated()) {
            throw new IllegalArgumentException("Cannot register. You already have a session started.");
        }
        this.userService.validateUserCredentials(email, password, nickName, password_repeated);
        this.userService.registerUser(email, password, nickName, password_repeated);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = {"/", APIAliases.LOGIN_ALIAS + "**"}, method = RequestMethod.GET)
    public ModelAndView getLoginPage() throws ServletException, IOException {
        if (SecurityUtils.isAuthenticated()) {
            final RedirectView view = new RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
            view.setExposeModelAttributes(false);
            return new ModelAndView(view);
        } else {
            final ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
            return modelAndView;
        }
    }

    @RequestMapping(value = {APIAliases.MAIN_PAGE_ALIAS}, method = RequestMethod.GET)
    public ModelAndView getMainPage() throws IOException, ServletException {
        if (SecurityUtils.isAuthenticated()) {
            return new ModelAndView(ServerConstants.MAIN_PAGE);
        } else {
            return new ModelAndView("redirect:" + APIAliases.LOGIN_ALIAS);
        }
    }

    @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS+"*"}, method = RequestMethod.GET)
    public ModelAndView getUploadPage()
            throws IOException, ServletException {
        if (SecurityUtils.isAuthenticated()) {
            return new ModelAndView(ServerConstants.UPLOAD_PAGE);
        } else {
            final ModelAndView modelAndView = new ModelAndView("redirect:" + APIAliases.LOGIN_ALIAS);
            return modelAndView;
        }

    }

    @RequestMapping(value = {APIAliases.REGISTRATION_ALIAS}, method = RequestMethod.GET)
    public ModelAndView getRegistrationPage()
            throws IOException, ServletException {
        if (SecurityUtils.isAuthenticated()) {
            final RedirectView view = new
                    RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
            view.setExposeModelAttributes(false);
            return new ModelAndView(view);
        } else {
            final ModelAndView modelAndView = new ModelAndView(ServerConstants.REGISTRATION_PAGE);
            return modelAndView;
        }

    }

    @Secured(Authorities.USER)
    @RequestMapping(value = {APIAliases.LOGOUT_ALIAS + "**"}, method = RequestMethod.GET)
    public ModelAndView logClientOut(final HttpServletRequest request, final HttpServletResponse response) {
        ServerUtil.invalidateSession(request, response);
        final ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
        return modelAndView;
    }


}
