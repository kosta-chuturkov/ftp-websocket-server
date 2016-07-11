package ftp.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ftp.core.common.model.User;
import ftp.core.common.util.ServerUtil;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;

@Controller("loginController")
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class);
    @Resource
    private UserService userService;

    @RequestMapping(value = {"/", APIAliases.LOGIN_ALIAS + "**"}, method = RequestMethod.GET)
    public ModelAndView getLoginPage(final HttpServletRequest request, final HttpServletResponse response) {

        try {
			// if (ServerUtil.checkUserSession(request, true)) {
			// final RedirectView view = new
			// RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
			// view.setExposeModelAttributes(false);
			// return new ModelAndView(view);
			// } else {
			// ServerUtil.invalidateSession(request, response);
                final ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
			return modelAndView;
			// }
        } catch (final Exception e) {
            logger.error("errror occured", e);
            throw new FtpServerException(e.getMessage());
        }
    }

    @RequestMapping(value = {APIAliases.LOGIN_ALIAS}, method = RequestMethod.POST)
    public ModelAndView authenticateClient(final HttpServletRequest request,
                                           @NotNull @ModelAttribute("email") final String email,
                                           @NotNull @ModelAttribute("pswd") final String password) {

        try {
            final ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
            final Long tokenByEmail = this.userService.getTokenByEmail(email);
            if (tokenByEmail == null) {
                modelAndView.addObject("errorMsg", "Invalid user credentials.");
                ServerUtil.removeEmailAndPasswordParams(modelAndView);
                return modelAndView;
            }
            final String encodedPassword = ServerUtil.digestRawPassword(password, ServerUtil.SALT,
                    tokenByEmail.toString());
            final User user = this.userService.findByEmailAndPassword(email, encodedPassword);
            if (user == null) {
                modelAndView.addObject("errorMsg", "Invalid user credentials.");
                ServerUtil.removeEmailAndPasswordParams(modelAndView);
                return modelAndView;
            } else {
                User.setCurrent(user);
                ServerUtil.startUserSession(request, email, encodedPassword, user.getRemainingStorage());
                final RedirectView view = new RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
                view.setExposeModelAttributes(false);
                return new ModelAndView(view);
            }
        } catch (final Exception e) {
            logger.error("errror occured", e);
            throw new FtpServerException(e.getMessage());
        }
    }

}
