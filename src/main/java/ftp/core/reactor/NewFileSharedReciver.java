package ftp.core.reactor;

import com.google.gson.Gson;
import ftp.core.websocket.dto.JsonResponse;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import reactor.bus.Event;
import reactor.fn.Consumer;

import java.io.IOException;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
public class NewFileSharedReciver implements Consumer<Event<JsonResponse>> {

    private final WebSocketSession webSocketSession;

    private final Gson gson;

    public NewFileSharedReciver(final WebSocketSession webSocketSession, final Gson gson) {
        this.webSocketSession = webSocketSession;
        this.gson = gson;
    }

    @Override
    public void accept(final Event<JsonResponse> event) {
        if (this.webSocketSession != null) {
            synchronized (this.webSocketSession) {
                final JsonResponse fileDto = event.getData();
                try {
                    if (this.webSocketSession.isOpen()) {
                        this.webSocketSession.sendMessage(new TextMessage(this.gson.toJson(fileDto)));
                    }
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
