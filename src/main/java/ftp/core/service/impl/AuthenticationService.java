package ftp.core.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.service.face.tx.UserService;

/**
 * Created by Kosta_Chuturkov on 6/10/2016.
 */
@Service
public class AuthenticationService {

	@Resource
	private UserService userService;

	public void authenticateClient(final HttpServletRequest request, final HttpServletResponse response) {
		final String email = ServerUtil.getSessionParam(request, ServerConstants.EMAIL_PARAMETER);
		final String password = ServerUtil.getSessionParam(request, ServerConstants.PASSWORD);
		final User current = this.userService.findByEmailAndPassword(email, password);
		if (current == null) {
			ServerUtil.sendJsonErrorResponce(response, "You must login first.");
		} else {
			User.setCurrent(current);
		}
	}
}
