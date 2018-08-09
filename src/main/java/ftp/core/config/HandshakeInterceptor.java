package ftp.core.config;

import ftp.core.constants.ServerConstants;
import ftp.core.api.UserSessionCounter;
import ftp.core.model.entities.User;
import ftp.core.service.face.tx.UserService;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * Created by Kosta_Chuturkov on 2/25/2016.
 */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

  private final UserService userService;

  private final UserSessionCounter userSessionCounter;

  public HandshakeInterceptor(UserService userService,
      UserSessionCounter userSessionCounter) {
    this.userService = userService;
    this.userSessionCounter = userSessionCounter;
  }

  @Override
  public boolean beforeHandshake(final ServerHttpRequest request,
      final ServerHttpResponse response, final WebSocketHandler wsHandler,
      final Map<String, Object> attributes) throws Exception {
    final HttpServletRequest servletRequest = ((ServletServerHttpRequest) request)
        .getServletRequest();
    final String email = getSessionParam(servletRequest, ServerConstants.EMAIL_PARAMETER);
    final String password = getSessionParam(servletRequest, ServerConstants.PASSWORD);
    final User current = this.userService.findByEmailAndPassword(email, password);
    if (current == null) {
      throw new RuntimeException("Invalid username or password.");
    }
    final HttpSession session = servletRequest.getSession(false);
    final Enumeration<String> attributeNames = session.getAttributeNames();
    while (attributeNames.hasMoreElements()) {
      final String nextElement = attributeNames.nextElement();
      final Object attribute = session.getAttribute(nextElement);
      attributes.put(nextElement, attribute);
    }
    attributes.put(ServerConstants.CURRENT_USER, current);
    this.userSessionCounter.addUserSession(current.getNickName());
    return super.beforeHandshake(request, response, wsHandler, attributes);
  }

  @Override
  public void afterHandshake(final ServerHttpRequest request,
      final ServerHttpResponse response, final WebSocketHandler wsHandler,
      final Exception ex) {
    super.afterHandshake(request, response, wsHandler, ex);
  }

  public String getSessionParam(final HttpServletRequest request, final String paramName) {
    final HttpSession session = request.getSession(false);
    return session == null ? null : (String) session.getAttribute(paramName);
  }
}