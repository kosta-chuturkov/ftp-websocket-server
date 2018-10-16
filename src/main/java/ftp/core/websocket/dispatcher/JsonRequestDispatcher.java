package ftp.core.websocket.dispatcher;

import com.google.gson.Gson;
import ftp.core.api.MessageConsumer;
import ftp.core.api.MessageSubscriptionService;
import ftp.core.constants.ServerConstants;
import ftp.core.model.entities.User;
import ftp.core.websocket.api.JsonTypedHandler;
import ftp.core.websocket.dto.JsonRequest;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.factory.JsonHandlerFactory;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
public class JsonRequestDispatcher extends TextWebSocketHandler {

  private final Logger logger = LoggerFactory.getLogger(JsonRequestDispatcher.class);

  @Resource
  private JsonHandlerFactory jsonHandlerFactory;

  @Resource
  private Gson gson;

  @Resource
  private MessageSubscriptionService messageSubscriptionService;

  @SendTo
  @Override
  public void afterConnectionEstablished(final WebSocketSession session) {
    Map<String, Object> sessionAttributes = session.getAttributes();
    final Object currentUser = sessionAttributes.get(ServerConstants.CURRENT_USER);
    if (currentUser != null) {
      final String currentUserNickName = ((User) currentUser).getNickName();
      MessageConsumer consumer = this.messageSubscriptionService
          .subscribe(currentUserNickName, session);
      sessionAttributes.put(session.getId(), consumer);
      this.logger.debug("Web Socket session added: " + session.toString());
    }
  }

  @Override
  public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
    Map<String, Object> sessionAttributes = session.getAttributes();
    final Object currentUser = sessionAttributes.get(ServerConstants.CURRENT_USER);
    if (currentUser != null) {
      MessageConsumer consumer = (MessageConsumer) sessionAttributes.get(session.getId());
      final String currentUserNickName = ((User) currentUser).getNickName();
      if (currentUserNickName != null && consumer != null) {
        this.messageSubscriptionService.unsubscribe(currentUserNickName, consumer);
        this.logger.debug("Web Socket session removed: " + session.toString());
      }
    }
  }

  @Override
  public void handleTextMessage(final WebSocketSession session, final TextMessage message)
      throws Exception {
    JsonRequest request = null;
    try {
      request = this.gson.fromJson(message.getPayload(), JsonRequest.class);
      setCurrentUser(session);
      final String methodToHandle = request.getMethod();
      final JsonTypedHandler handlerByType = this.jsonHandlerFactory
          .getHandlerByType(methodToHandle);
      final JsonResponse jsonResponse = handlerByType.handleRequestAndReturnJson(request);
      final String response = this.gson.toJson(jsonResponse);
      session.sendMessage(new TextMessage(response));
    } catch (final Exception e) {
      this.logger.error("error handling ws message", e);
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
    }
  }

  private void setCurrentUser(final WebSocketSession session) {
    final User currentUser = (User) session.getAttributes().get(ServerConstants.CURRENT_USER);
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(),
            currentUser.getAuthorities()));
  }


}
