package ftp.core.config;

import javax.servlet.http.HttpSessionListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ftp.core.listener.CustomHttpSessionListener;

/**
 * Created by Kosta_Chuturkov on 2/10/2016.
 */
@Configuration
public class SessionListenerConfig {
    private String str;

    @Bean
    public HttpSessionListener httpSessionListener(){
        return new CustomHttpSessionListener();
    }
}
