package ftp.core.config;

import com.google.gson.JsonParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kosta on 4.6.2016 г..
 */
@Configuration
public class JsonConfig {

  @Bean(name = "jsonParser")
  public JsonParser jsonParser() {
    return new JsonParser();
  }
}
