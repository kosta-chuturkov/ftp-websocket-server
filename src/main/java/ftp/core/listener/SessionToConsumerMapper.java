package ftp.core.listener;

import ftp.core.service.impl.EventService;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */

@Service("sessionToConsumerMapper")
public class SessionToConsumerMapper {

  @Value("${ftp.server.redis.namespace}")
  private String redisNamespace;

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionToConsumerMapper.class);

  @Resource
  private EventService eventService;

  private final RedisTemplate<String, Integer> redisTemplate;

  @Autowired
  public SessionToConsumerMapper(RedisTemplate<String, Integer> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  private ValueOperations<String, Integer> getValueOps() {
    return redisTemplate.opsForValue();
  }

  private Integer decrementAndGet(String key) {
    return getValueOps().increment(key, -1L).intValue();
  }

  private Integer incrementAndGet(String key) {
    return getValueOps().increment(key, 1L).intValue();
  }

  public final void addConsumer(String topic) {
    Integer currentSessionsCout = incrementAndGet(redisNamespace.concat(":" + topic));
    LOGGER.info("Adding session for [" + topic + "] = [" + currentSessionsCout + "]");

  }

  public final void removeConsumer(String topic) {
    Integer currentSessionsCount = decrementAndGet(redisNamespace.concat(":" + topic));
    LOGGER.info("Removed session for [" + topic + "] = [" + currentSessionsCount + "]");
    if (currentSessionsCount == 0) {
      this.eventService.unregisterConsumer(topic);
      LOGGER.info("Unregistered consumer for [" + topic + "]");
    }
  }
}
