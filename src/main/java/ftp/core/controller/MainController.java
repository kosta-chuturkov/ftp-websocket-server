package ftp.core.controller;

import ftp.core.common.util.ServerUtil;
import ftp.core.constants.APIAliases;
import ftp.core.constants.ServerConstants;
import ftp.core.service.face.tx.FtpServerException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MainController {

	private static final Logger logger = Logger.getLogger(MainController.class);

	@RequestMapping(value = { APIAliases.MAIN_PAGE_ALIAS }, method = RequestMethod.GET)
	public ModelAndView getLoginPage(final HttpServletRequest request, final HttpServletResponse response)
					throws IOException {
		try {
			if (ServerUtil.userHasSession(request, true)) {
				return new ModelAndView(ServerConstants.MAIN_PAGE);
			} else {
				return new ModelAndView("redirect:" + APIAliases.LOGIN_ALIAS);
			}
		} catch (final Exception e) {
			logger.error("error", e);
			throw new FtpServerException(e.getMessage());
		}
	}
}
