package org.romanzhula.office.configurations;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.romanzhula.office.handlers.OfficeWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import utils.helpers.MessageConverter;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class OfficeWebSocketConfig implements WebSocketConfigurer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConverter messageConverter;
    private final Cache<String, WebSocketSession> sessionCache;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(new OfficeWebSocketHandler(kafkaTemplate, messageConverter, sessionCache), "/websocket")
                .setAllowedOrigins("*")
        ;
    }

}
