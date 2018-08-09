package ftp.core.session;

import ftp.core.api.UserSessionCounter;
import ftp.core.service.impl.SchedulingService;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
@Profile("prod")
@Service("redisSessionCounter")
public class RedisSessionCounter
    implements UserSessionCounter {

  @Value("${ftp.server.redis.namespace}")
  private String redisNamespace;

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisSessionCounter.class);

  @Resource
  private SchedulingService schedulingService;

  private final RedisTemplate<String, Integer> redisTemplate;

  @Autowired
  public RedisSessionCounter(RedisTemplate<String, Integer> redisTemplate) {
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

  @Override
  public final void addUserSession(String userName) {
    Integer currentSessionsCout = incrementAndGet(redisNamespace.concat(":" + userName));
    LOGGER.info("Adding session for [" + userName + "] = [" + currentSessionsCout + "]");

  }

  @Override
  public final void removeUserSession(String userName) {
    Integer currentSessionsCount = decrementAndGet(redisNamespace.concat(":" + userName));
    LOGGER.info("Removed session for [" + userName + "] = [" + currentSessionsCount + "]");
    if (currentSessionsCount == 0) {
      this.schedulingService.unsubscribe(userName);
      LOGGER.info("Unregistered consumer for [" + userName + "]");
    }
  }
}
