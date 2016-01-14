package ftp.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {



/*	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getJsonRpcHandler(), "/echo", "/echo-issue4");
		registry.addHandler(getJsonRpcHandler(), "/sockjs/echo").withSockJS();
		registry.addHandler(getJsonRpcHandler(), "/sockjs/echo-issue4").withSockJS().setHttpMessageCacheSize(20000);
		System.out.println("Web socket registry called!");
	}*/



	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		// configurer.enable();
	}

	public void registerWebSocketHandlers(WebSocketHandlerRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

}
