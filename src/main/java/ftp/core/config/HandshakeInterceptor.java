package ftp.core.config;

import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.common.util.ServerUtil;
import ftp.core.listener.SessionToConsumerMapper;
import ftp.core.service.face.tx.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Kosta_Chuturkov on 2/25/2016.
 */
@Configuration
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private SessionToConsumerMapper sessionToConsumerMapper;

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