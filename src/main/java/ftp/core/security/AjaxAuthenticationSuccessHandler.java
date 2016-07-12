package ftp.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import ftp.core.common.model.User;
import ftp.core.common.util.ServerUtil;
import ftp.core.constants.APIAliases;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Component
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication)
        throws IOException, ServletException {
		final String email = User.getCurrent().getEmail();
		final String password = User.getCurrent().getPassword();
		final long remainingStorage = User.getCurrent().getRemainingStorage();
        ServerUtil.startUserSession(request, email, password, remainingStorage);
        response.setStatus(HttpServletResponse.SC_OK);
    }


}
