package ftp.core.config;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 20, redisNamespace = "ftp-server")
public class RedisConfig {

  @Value("${ftp.server.redis.port:6379}")
  private Integer redisPort;

  @Value("${ftp.server.redis.host:localhost}")
  private String redisHost;

  @Bean
  RedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
    jedisConnectionFactory.setHostName(redisHost);
    jedisConnectionFactory.setPort(redisPort);
    return jedisConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, Integer> redisTemplate() {
    RedisTemplate<String, Integer> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
    template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
    return template;
  }

  @Bean
  RedisMessageListenerContainer redisContainer() {
    RedisMessageListenerContainer container
        = new RedisMessageListenerContainer();
    container.setConnectionFactory(jedisConnectionFactory());
    return container;
  }

  @PostConstruct
  public void init(){
    //Will cause the app startup to fail if a connection to the Redis server cannot be established
    redisTemplate().dump("test");
  }
}
