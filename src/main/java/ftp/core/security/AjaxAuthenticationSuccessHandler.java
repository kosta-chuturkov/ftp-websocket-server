package ftp.core.security;

import ftp.core.constants.ServerConstants;
import ftp.core.model.entities.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Component
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) {
        final User springSecurityUser = (User) authentication.getPrincipal();
        final String email = springSecurityUser.getEmail();
        final String password = springSecurityUser.getPassword();
        final String nickName = springSecurityUser.getNickName();
        final long remainingStorage = springSecurityUser.getRemainingStorage();
        setSessionProperties(request, response, nickName, email, password, remainingStorage);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void setSessionProperties(final HttpServletRequest request, HttpServletResponse response,
                                     String nickName, final String email, final String password, final long storage) {
        final HttpSession session = request.getSession();
        session.setAttribute(ServerConstants.EMAIL_PARAMETER, email);
        session.setAttribute(ServerConstants.PASSWORD, password);
        session.setAttribute(ServerConstants.HOST, request.getServerName());
        session.setAttribute(ServerConstants.PORT, request.getServerPort());
        session
                .setAttribute(ServerConstants.STORAGE_PARAMETER, FileUtils.byteCountToDisplaySize(storage));
        session.setAttribute(ServerConstants.MAX_STORAGE_PARAMETER,
                FileUtils.byteCountToDisplaySize(ServerConstants.UPLOAD_LIMIT));
        session.setMaxInactiveInterval(30 * 60);
        response.addCookie(new Cookie(ServerConstants.SESSION_ID_PARAMETER, session.getId()));


    }

}
