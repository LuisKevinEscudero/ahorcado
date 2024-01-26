package com.kevin.ahorcado.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // Habilitar un broker simple en el destino "/topic"
        config.setApplicationDestinationPrefixes("/app");  // Establecer el prefijo de destino de la aplicación
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-endpoint")
                .setAllowedOrigins("http://localhost:3000")  // Reemplazar con el origen de tu aplicación React
                .withSockJS();  // Habilitar SockJS para la compatibilidad con navegadores sin soporte WebSocket
    }
}

