package ftp.core.controller;

import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.service.face.tx.UserService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static ftp.core.util.ServerUtil.userHasSession;

@RestController("loginController")
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class);
    @Resource
    private UserService userService;

    @RequestMapping(value = {"/", APIAliases.LOGIN_ALIAS + "**"}, method = RequestMethod.GET)
    public ModelAndView getLoginPage(final HttpServletRequest request, WebRequest webr) throws ServletException, IOException {
        if (userHasSession(request, false)) {
            final RedirectView view = new RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
            view.setExposeModelAttributes(false);
            return new ModelAndView(view);
        } else {
            final ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
            return modelAndView;
        }
    }
}

