package ftp.core.controller;

import java.io.IOException;

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

@Controller
public class RegistrationController {

	private static final Logger logger = Logger.getLogger(RegistrationController.class);
	@Resource
	private UserService userService;

	@RequestMapping(value = {APIAliases.REGISTRATION_ALIAS}, method = RequestMethod.GET)
	public ModelAndView getRegistrationPage(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		try {
			// if (ServerUtil.checkUserSession(request, true)) {
			// final RedirectView view = new
			// RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
			// view.setExposeModelAttributes(false);
			// return new ModelAndView(view);
			// } else {
			// ServerUtil.invalidateSession(request, response);
				final ModelAndView modelAndView = new ModelAndView(ServerConstants.REGISTRATION_PAGE);
				return modelAndView;
			// }
		} catch (final Exception e) {
			logger.error("error", e);
			throw new FtpServerException(e.getMessage());
		}
	}

	@RequestMapping(value = {APIAliases.REGISTRATION_ALIAS}, method = RequestMethod.POST)
	public ModelAndView register(final HttpServletRequest request, @NotNull @ModelAttribute("email") final String email,
								 @NotNull @ModelAttribute("pswd") final String password, @NotNull @ModelAttribute("nickname") final String nickName,
								 @NotNull @ModelAttribute("password_repeated") final String password_repeated) throws IOException {
		final ModelAndView modelAndView = new ModelAndView(ServerConstants.REGISTRATION_PAGE);
		try {
			this.userService.validateUserCredentials(email, password, nickName, password_repeated, modelAndView);

			final Long id = this.userService.registerUser(email, password, nickName, password_repeated, modelAndView);
			if (id == null) {
				modelAndView.addObject("errorMsg", "Unable to register with this credentials. Please try again.");
			} else {
				final User user = this.userService.findOne(id);
				User.setCurrent(user);
				ServerUtil.startUserSession(request, email, user.getPassword(), user.getRemainingStorage());
				final RedirectView view = new RedirectView(APIAliases.MAIN_PAGE_ALIAS, true);
				view.setExposeModelAttributes(false);
				return new ModelAndView(view);
			}
		} catch (final IllegalArgumentException e) {
			modelAndView.addObject("errorMsg", e.getMessage());
		}
		return modelAndView;
	}

}
