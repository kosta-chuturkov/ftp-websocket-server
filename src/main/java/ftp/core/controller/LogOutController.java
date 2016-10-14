package ftp.core.controller;

import ftp.core.common.util.ServerUtil;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.security.Authorities;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LogOutController {

	@Secured(Authorities.USER)
	@RequestMapping(value = {APIAliases.LOGOUT_ALIAS + "**"}, method = RequestMethod.GET)
	public ModelAndView logClientOut(final HttpServletRequest request, final HttpServletResponse response){
		ServerUtil.invalidateSession(request, response);
		final ModelAndView modelAndView = new ModelAndView(ServerConstants.NEW_CLIENT_LOGIN_PAGE);
		return modelAndView;
	}
}
