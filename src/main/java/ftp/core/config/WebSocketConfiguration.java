//package ftp.core.config;
//
//import ftp.core.service.face.tx.UserService;
//import ftp.core.websocket.dispatcher.JsonRequestDispatcher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
//
///**
// * Created by Kosta_Chuturkov on 2/10/2016.
// */
//@Configuration
//@EnableWebSocket
//public class WebSocketConfiguration implements WebSocketConfigurer, WebMvcConfigurer {
//
//    @Autowired
//    private UserService userService;
//
//    @Override
//    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
//        registry
//                .addHandler(echoWebSocketHandler(), "/sockjs/files")
//                .withSockJS()
//                .setInterceptors(handshakeInterceptor());
//    }
//
//    @Bean
//    public WebSocketHandler echoWebSocketHandler() {
//        return new PerConnectionWebSocketHandler(JsonRequestDispatcher.class);
//    }
//
//    @Bean
//    public org.springframework.web.socket.server.HandshakeInterceptor handshakeInterceptor() {
//        return new HandshakeInterceptor(this.userService);
//    }
//
//    // Allow serving HTML files through the default Servlet
//
//    @Override
//    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }
//}
