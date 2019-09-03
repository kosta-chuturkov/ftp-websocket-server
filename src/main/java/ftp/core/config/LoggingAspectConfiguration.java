package ftp.core.config;

import ftp.core.logging.LoggingAspect;
import ftp.core.profiles.Profiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

//  @Bean
////  @Profile(Profiles.PRODUCTION)
//  public LoggingAspect loggingAspect() {
//    return new LoggingAspect();
//  }
}
