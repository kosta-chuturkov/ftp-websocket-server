package ftp.core.security;

import com.google.gson.Gson;
import ftp.core.model.dto.ErrorDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static ftp.core.util.ServerUtil.getErrorDetailsWrapper;

/**
 * Returns a 401 error code (Unauthorized) to the client, when Ajax authentication fails.
 */
@Component
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private Gson gson;

    @Autowired
    public AjaxAuthenticationFailureHandler(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorDetails errorDetailsWrapper = getErrorDetailsWrapper(exception, HttpStatus.UNAUTHORIZED, "Provided password email combination is invalid", "/api/v1/login");
        PrintWriter writer = response.getWriter();
        writer.write(this.gson.toJson(errorDetailsWrapper));
        writer.flush();
        //throw new ApiAuthenticationException("exception");
    }
}
