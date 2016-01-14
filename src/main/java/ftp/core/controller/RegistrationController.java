package ftp.core.controller;

import java.io.IOException;
import java.io.Serializable;
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

@Controller
public class RegistrationController {

	@Resource
	private UserService userService;

	private static final Logger logger = Logger.getLogger(RegistrationController.class);

	@RequestMapping(value = { "/register" }, method = RequestMethod.GET)
	public ModelAndView getRegistrationPage(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			if (ServerUtil.checkUserSession(request, true)) {
				RedirectView view = new RedirectView(ServerConstants.MAIN_ALIAS, true);
				view.setExposeModelAttributes(false);
				return new ModelAndView(view);
			} else {
				ServerUtil.invalidateSession(request, response);
				ModelAndView modelAndView = new ModelAndView(ServerConstants.REGISTRATION_PAGE);
				return modelAndView;
			}
		} catch (Exception e) {
			logger.error("error", e);
			throw new FtpServerException(e.getMessage());
		}
	}

	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public ModelAndView register(HttpServletRequest request, @NotNull @ModelAttribute("email") String email,
			@NotNull @ModelAttribute("pswd") String password, @NotNull @ModelAttribute("nickname") String nickName,
			@NotNull @ModelAttribute("password_repeated") String password_repeated) throws IOException {
		ModelAndView modelAndView = new ModelAndView(ServerConstants.REGISTRATION_PAGE);
		try {
			userService.validateUserCredentials(email, password, nickName, password_repeated, modelAndView);

			Number id = userService.registerUser(email, password, nickName, password_repeated, modelAndView);
			if (id == null) {
				modelAndView.addObject("errorMsg", "Unable to register with this credentials. Please try again.");
			} else {
				User user = (User) userService.findOne(id);
				User.setCurrent(user);
				ServerUtil.startUserSession(request, email, user.getPassword(), user.getRemainingStorage());
				RedirectView view = new RedirectView(ServerConstants.MAIN_ALIAS, true);
				view.setExposeModelAttributes(false);
				return new ModelAndView(view);
			}
		} catch (IllegalArgumentException e) {
			modelAndView.addObject("errorMsg", e.getMessage());
		}
		return modelAndView;
	}

}
