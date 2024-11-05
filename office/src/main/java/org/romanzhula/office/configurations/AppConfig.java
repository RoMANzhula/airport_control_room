package org.romanzhula.office.configurations;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import utils.helpers.MessageConverter;

import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new MessageConverter();
    }

    @Bean
    public Cache<String, WebSocketSession> sessionCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build()
        ;
    }

}
