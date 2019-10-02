package ftp.core.api;

import org.springframework.web.socket.WebSocketSession;

public interface MessageSubscriptionService {

    MessageConsumer subscribe(String topic, WebSocketSession session);

    void unsubscribe(String topic, MessageConsumer messageConsumer);
}
