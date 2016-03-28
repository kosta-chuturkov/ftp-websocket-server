package ftp.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ftp.core.websocket.dispatcher.JsonRequestDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

/**
 * Created by Kosta_Chuturkov on 2/10/2016.
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(  echoWebSocketHandler(), "/echo");
        registry.addHandler(echoWebSocketHandler(), "/sockjs/echo").withSockJS().setInterceptors(handshakeInterceptor());
    }

    @Bean
    public WebSocketHandler echoWebSocketHandler() {
        return new PerConnectionWebSocketHandler(JsonRequestDispatcher.class);
    }

    @Bean
    public org.springframework.web.socket.server.HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptor();
    }

    // Allow serving HTML files through the default Servlet

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public Gson gson(){
        return new GsonBuilder().serializeNulls().create();
    }
}
