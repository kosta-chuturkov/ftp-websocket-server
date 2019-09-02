package ftp.core.redis;

import com.google.gson.Gson;
import ftp.core.api.MessageConsumer;
import ftp.core.api.MessageSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class RedisMessageSubscriptionService implements MessageSubscriptionService{

  private final Gson gson;
  private final RedisMessageListenerContainer redisMessageListenerContainer;

  @Autowired
  public RedisMessageSubscriptionService(Gson gson,
      RedisMessageListenerContainer redisMessageListenerContainer) {
    this.gson = gson;
    this.redisMessageListenerContainer = redisMessageListenerContainer;
  }

  @Override
  public MessageConsumer subscribe(String topic, WebSocketSession session) {
    RedisMessageConsumer redisMessageConsumer = new RedisMessageConsumer(session, this.gson);
    redisMessageListenerContainer.addMessageListener(redisMessageConsumer, new ChannelTopic(topic));
    return redisMessageConsumer;
  }

  @Override
  public void unsubscribe(String topic, MessageConsumer messageConsumer) {
    redisMessageListenerContainer.removeMessageListener((MessageListener) messageConsumer, new ChannelTopic(topic));
  }
}
