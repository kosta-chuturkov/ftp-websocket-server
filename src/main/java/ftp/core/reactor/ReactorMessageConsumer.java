package ftp.core.reactor;

import com.google.gson.Gson;
import ftp.core.api.MessageConsumer;
import ftp.core.websocket.dto.JsonResponse;
import java.io.IOException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import reactor.bus.Event;
import reactor.fn.Consumer;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
public class ReactorMessageConsumer implements Consumer<Event<String>>, MessageConsumer {

  private final WebSocketSession webSocketSession;

  public ReactorMessageConsumer(final WebSocketSession webSocketSession) {
    this.webSocketSession = webSocketSession;
  }

  @Override
  public void accept(final Event<String> event) {
    if (this.webSocketSession != null) {
      synchronized (this.webSocketSession) {
        try {
          if (this.webSocketSession.isOpen()) {
            this.webSocketSession.sendMessage(new TextMessage(event.getData()));
          }
        } catch (final IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
