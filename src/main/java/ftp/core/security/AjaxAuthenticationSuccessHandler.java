package ftp.core.security;

import ftp.core.model.entities.User;
import ftp.core.util.ServerUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Component
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication)
            throws IOException, ServletException {
        final User springSecurityUser = (User) authentication.getPrincipal();
        final String email = springSecurityUser.getEmail();
        final String password = springSecurityUser.getPassword();
        final String nickName = springSecurityUser.getNickName();
        final long remainingStorage = springSecurityUser.getRemainingStorage();
        ServerUtil.setSessionProperties(request, response, nickName, email, password, remainingStorage);
        response.setStatus(HttpServletResponse.SC_OK);
    }


}
