package ftp.core.security;

import ftp.core.util.ServerUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Returns a 401 error code (Unauthorized) to the client.
 */
@Component
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final Logger log = LoggerFactory.getLogger(Http401UnauthorizedEntryPoint.class);

    /**
     * Always returns a 401 error code to the client.
     */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException arg2)
            throws IOException,
            ServletException {

        this.log.debug("Pre-authenticated entry point called. Rejecting access");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ServerUtil.invalidateSession(request, response);
        redirect("/login", response);
    }

    public void redirect(final String resourceName, final HttpServletResponse response)
            throws ServletException, IOException {
    }
}
