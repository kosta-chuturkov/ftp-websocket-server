package ftp.core.redis;

import com.google.gson.Gson;
import ftp.core.api.MessagePublishingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageProducer implements MessagePublishingService {

  private RedisTemplate redisTemplate;
  private final Gson gson;

  @Autowired
  public RedisMessageProducer(
      RedisTemplate redisTemplate, Gson gson) {
    this.redisTemplate = redisTemplate;
    this.gson = gson;
  }

  public void publish(String topic, Object message) {
    redisTemplate.convertAndSend(topic, gson.toJson(message));
  }
}
