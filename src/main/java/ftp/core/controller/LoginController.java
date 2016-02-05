package ftp.core.controller;

import java.math.BigDecimal;

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
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.service.face.tx.UserService;

@Controller("loginController")
public class LoginController {

	private static final Logger logger = Logger.getLogger(LoginController.class);
	@Resource
	private UserService userService;

	@RequestMapping(value = {
			"/", "/login**"
	}, method = RequestMethod.GET)
	public ModelAndView getLoginPage(HttpServletRequest request, HttpServletResponse response) {

		try {
			if (ServerUtil.checkUserSession(request, true)) {
				RedirectView view = new RedirectView(ServerConstants.MAIN_ALIAS, true);
				view.setExposeModelAttributes(false);
				return new ModelAndView(view);
			} else {
				ServerUtil.invalidateSession(request, response);
				ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
				return modelAndView;
			}
		} catch (Exception e) {
			logger.error("errror occured", e);
			throw new FtpServerException(e.getMessage());
		}
	}

	@RequestMapping(value = {
			"/login"
	}, method = RequestMethod.POST)
	public ModelAndView logIn(HttpServletRequest request, @NotNull @ModelAttribute("email") String email,
			@NotNull @ModelAttribute("pswd") String password) {

		try {
			ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
			BigDecimal tokenByEmail = userService.getTokenByEmail(email);
			if (tokenByEmail == null) {
				modelAndView.addObject("errorMsg", "Invalid user credentials.");
				ServerUtil.removeEmailAndPasswordParams(modelAndView);
				return modelAndView;
			}
			String encodedPassword = ServerUtil.digestRawPassword(password, ServerUtil.SALT,
					tokenByEmail.toPlainString());
			User user = userService.findByEmailAndPassword(email, encodedPassword);
			if (user == null) {
				modelAndView.addObject("errorMsg", "Invalid user credentials.");
				ServerUtil.removeEmailAndPasswordParams(modelAndView);
				return modelAndView;
			} else {
				User.setCurrent(user);
				ServerUtil.startUserSession(request, email, encodedPassword, user.getRemainingStorage());
				RedirectView view = new RedirectView(ServerConstants.MAIN_ALIAS, true);
				view.setExposeModelAttributes(false);
				return new ModelAndView(view);
			}
		} catch (Exception e) {
			logger.error("errror occured", e);
			throw new FtpServerException(e.getMessage());
		}
	}

}
