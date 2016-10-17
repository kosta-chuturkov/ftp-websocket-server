package ftp.core.config;

import ftp.core.constants.ServerConstants;
import ftp.core.listener.SessionToConsumerMapper;
import ftp.core.model.entities.User;
import ftp.core.service.face.tx.UserService;
import ftp.core.util.ServerUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by Kosta_Chuturkov on 2/25/2016.
 */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private UserService userService;

    private SessionToConsumerMapper sessionToConsumerMapper;

    public HandshakeInterceptor(UserService userService, SessionToConsumerMapper sessionToConsumerMapper) {
        this.userService = userService;
        this.sessionToConsumerMapper = sessionToConsumerMapper;
    }

    @Override
    public boolean beforeHandshake(final ServerHttpRequest request,
                                   final ServerHttpResponse response, final WebSocketHandler wsHandler,
                                   final Map<String, Object> attributes) throws Exception {
        final HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        final String email = ServerUtil.getSessionParam(servletRequest, ServerConstants.EMAIL_PARAMETER);
        final String password = ServerUtil.getSessionParam(servletRequest, ServerConstants.PASSWORD);
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
        this.sessionToConsumerMapper.addConsumer(current.getNickName());
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(final ServerHttpRequest request,
                               final ServerHttpResponse response, final WebSocketHandler wsHandler,
                               final Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}