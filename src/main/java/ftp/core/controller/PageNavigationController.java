package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.model.entities.User;
import ftp.core.security.Authorities;
import ftp.core.service.face.tx.UserService;
import ftp.core.service.impl.EventService;
import ftp.core.util.ServerUtil;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PageNavigationController {

  @Resource
  private UserService userService;

  @Resource
  private EventService eventService;


  @RequestMapping(value = {APIAliases.REGISTRATION_ALIAS}, method = RequestMethod.POST)
  public DeferredResult<ResponseEntity<?>> registerUser(
      @NotNull @ModelAttribute("email") final String email,
      @NotNull @ModelAttribute("pswd") final String password,
      @NotNull @ModelAttribute("nickname") final String nickName,
      @NotNull @ModelAttribute("password_repeated") final String password_repeated)
      throws IOException, ServletException {
    return this.eventService
        .scheduleTaskToReactor(() -> register(email, password, nickName, password_repeated),
            10000L);
  }

  @RequestMapping(value = {"/", APIAliases.LOGIN_ALIAS + "**"}, method = RequestMethod.GET)
  public DeferredResult<ModelAndView> getLoginPage() throws ServletException, IOException {
    return this.eventService
        .scheduleTaskToReactor(() -> getPage(ServerConstants.NEW_CLIENT_LOGIN_PAGE), 10000L);
  }

  @RequestMapping(value = {APIAliases.MAIN_PAGE_ALIAS}, method = RequestMethod.GET)
  public DeferredResult<ModelAndView> getMainPage() throws IOException, ServletException {
    return this.eventService
        .scheduleTaskToReactor(this::mainPage, 10000L);
  }

  @RequestMapping(value = {APIAliases.UPLOAD_FILE_ALIAS + "*"}, method = RequestMethod.GET)
  public DeferredResult<ModelAndView> getUploadPage()
      throws IOException, ServletException {
    return this.eventService
        .scheduleTaskToReactor(this::uploadPage, 10000L);

  }

  @RequestMapping(value = {APIAliases.REGISTRATION_ALIAS}, method = RequestMethod.GET)
  public DeferredResult<ModelAndView> getRegistrationPage()
      throws IOException, ServletException {
    return this.eventService
        .scheduleTaskToReactor(() -> getPage(ServerConstants.REGISTRATION_PAGE), 10000L);

  }

  @Secured(Authorities.USER)
  @RequestMapping(value = {APIAliases.LOGOUT_ALIAS + "**"}, method = RequestMethod.GET)
  public DeferredResult<ModelAndView> logClientOut(final HttpServletRequest request,
      final HttpServletResponse response) {
    return this.eventService
        .scheduleTaskToReactor(() -> logOut(request, response), 10000L);
  }

  private ModelAndView logOut(HttpServletRequest request, HttpServletResponse response) {
    ServerUtil.invalidateSession(request, response);
    return new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
  }

  private ModelAndView uploadPage() {
    if (User.isAuthenticated()) {
      return new ModelAndView(ServerConstants.UPLOAD_PAGE);
    } else {
      return new ModelAndView("redirect:" + APIAliases.LOGIN_ALIAS);
    }
  }

  private ModelAndView mainPage() {
    if (User.isAuthenticated()) {
      return new ModelAndView(ServerConstants.MAIN_PAGE);
    } else {
      return new ModelAndView("redirect:" + APIAliases.LOGIN_ALIAS);
    }
  }

  private ResponseEntity<?> register(@NotNull @ModelAttribute("email") String email,
      @NotNull @ModelAttribute("pswd") String password,
      @NotNull @ModelAttribute("nickname") String nickName,
      @NotNull @ModelAttribute("password_repeated") String password_repeated) {
    if (User.isAuthenticated()) {
      throw new IllegalArgumentException("Cannot register. You already have a session started.");
    }
    this.userService.validateUserCredentials(email, password, nickName, password_repeated);
    this.userService.registerUser(email, nickName, password, password_repeated);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  private ModelAndView getPage(String newClientLoginPage) {
    if (User.isAuthenticated()) {
      final RedirectView view = new RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
      view.setExposeModelAttributes(false);
      return new ModelAndView(view);
    } else {
      return new ModelAndView(newClientLoginPage);
    }
  }

}
