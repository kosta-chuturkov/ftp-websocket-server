package ftp.core.websocket.dispatcher;

import com.google.gson.Gson;
import ftp.core.common.model.User;
import ftp.core.common.util.ServerConstants;
import ftp.core.listener.SessionToConsumerMapper;
import ftp.core.reactor.NotificationDispatcher;
import ftp.core.service.impl.EventService;
import ftp.core.websocket.api.JsonTypedHandler;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.factory.JsonHandlerFactory;
import org.apache.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public class JsonRequestDispatcher extends TextWebSocketHandler {

    private final Logger logger = Logger.getLogger(JsonRequestDispatcher.class);

    @Resource
    private JsonHandlerFactory jsonHandlerFactory;

    @Resource
    private Gson gson;

    @Resource
    private EventService eventService;

    @Resource
    private SessionToConsumerMapper sessionToConsumerMapper;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        final Object currentUser = session.getAttributes().get(ServerConstants.CURRENT_USER);
        if (currentUser != null) {
            final NotificationDispatcher notificationDispatcher = new NotificationDispatcher(session, this.gson);
            final String currentUserNickName = ((User) currentUser).getNickName();
            this.eventService.listen(currentUserNickName, notificationDispatcher);
        }
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
        final Object currentUser = session.getAttributes().get(ServerConstants.CURRENT_USER);
        if (currentUser != null) {
            this.sessionToConsumerMapper.removeConsumer(((User) currentUser).getNickName());
            this.logger.debug("Web Socket session removed: " + session.toString());
        }
    }

    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        JsonRequest request = null;
        try {
            request = this.gson.fromJson(message.getPayload(), JsonRequest.class);
            setCurrentUser(session);
            final String methodToHandle = request.getMethod();
            final JsonTypedHandler handlerByType = this.jsonHandlerFactory.getHandlerByType(methodToHandle);
            final JsonResponse jsonResponse = handlerByType.getJsonResponse(request);
            final String response = this.gson.toJson(jsonResponse);
            session.sendMessage(new TextMessage(response));
        } catch (final Exception e) {
            this.logger.error(e);
            final JsonResponse jsonResponse;
            if (request == null) {
                jsonResponse = new JsonResponse();
                jsonResponse.setError("Unable to parse request.");
            } else {
                jsonResponse = new JsonResponse();
                jsonResponse.setError(e.getMessage());
                jsonResponse.setResponseMethod(request.getMethod());
            }
            session.sendMessage(new TextMessage(this.gson.toJson(jsonResponse)));
            e.printStackTrace();
        }
    }

    private void setCurrentUser(final WebSocketSession session) {
        final Object currentUser = session.getAttributes().get(ServerConstants.CURRENT_USER);
        User.setCurrent(currentUser != null ? (User) currentUser : null);
    }


}
