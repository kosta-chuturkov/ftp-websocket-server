package ftp.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;

@Controller
public class LogOutController {

	@RequestMapping(value = { "/logout**" }, method = RequestMethod.GET)
	public ModelAndView getLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServerUtil.invalidateSession(request, response);
		ModelAndView modelAndView = new ModelAndView("redirect:" + ServerConstants.LOGIN_ALIAS);
		return modelAndView;
	}
}
