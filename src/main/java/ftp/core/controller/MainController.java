package ftp.core.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.service.face.tx.FtpServerException;

@Controller
public class MainController {

	private static final Logger logger = Logger.getLogger(MainController.class);

	@RequestMapping(value = { "/main/*" }, method = RequestMethod.GET)
	public ModelAndView getLoginPage(final HttpServletRequest request, final HttpServletResponse response)
					throws IOException {
		try {
			if (ServerUtil.checkUserSession(request, true)) {
				return new ModelAndView(ServerConstants.MAIN_PAGE);
			} else {
				ServerUtil.invalidateSession(request, response);
				return new ModelAndView("redirect:" + ServerConstants.LOGIN_ALIAS);
			}
		} catch (final Exception e) {
			logger.error("error", e);
			throw new FtpServerException(e.getMessage());
		}
	}
}
