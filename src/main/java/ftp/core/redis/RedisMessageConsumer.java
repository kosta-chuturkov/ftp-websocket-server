package ftp.core.redis;

import com.google.gson.Gson;
import ftp.core.api.MessageConsumer;
import ftp.core.websocket.dto.JsonResponse;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class RedisMessageConsumer implements MessageListener, MessageConsumer {

    private final WebSocketSession webSocketSession;

    private final Gson gson;

    public RedisMessageConsumer(WebSocketSession webSocketSession, Gson gson) {
        this.webSocketSession = webSocketSession;
        this.gson = gson;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (this.webSocketSession != null) {
            synchronized (this.webSocketSession) {
                try {
                    if (this.webSocketSession.isOpen()) {
                        this.webSocketSession.sendMessage(new TextMessage(new String(message.getBody(), Charset.forName("utf-8"))));
                    }
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
