package ftp.core.controller;

import ftp.core.common.util.ServerUtil;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller("loginController")
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class);
    @Resource
    private UserService userService;

    @RequestMapping(value = {"/", APIAliases.LOGIN_ALIAS + "**"}, method = RequestMethod.GET)
    public ModelAndView getLoginPage(final HttpServletRequest request, final HttpServletResponse response) {

        try {
            if (ServerUtil.userHasSession(request, true)) {
                final RedirectView view = new
                        RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
                view.setExposeModelAttributes(false);
                return new ModelAndView(view);
            } else {
                ServerUtil.invalidateSession(request, response);
                final ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
			return modelAndView;
            }
        } catch (final Exception e) {
            logger.error("errror occured", e);
            throw new FtpServerException(e.getMessage());
        }
    }

}
