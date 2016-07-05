package ftp.core.controller;

import ftp.core.common.util.ServerUtil;
import ftp.core.constants.APIAliases;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LogOutController {

	@RequestMapping(value = {APIAliases.LOGOUT_ALIAS + "**"}, method = RequestMethod.GET)
	public ModelAndView logClientOut(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		ServerUtil.invalidateSession(request, response);
		final ModelAndView modelAndView = new ModelAndView("redirect:" + APIAliases.LOGIN_ALIAS);
		return modelAndView;
	}
}
