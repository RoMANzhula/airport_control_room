package org.romanzhula.office.handlers;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import possible_messages.OfficeStateMessage;
import utils.helpers.MessageConverter;

@Component
@RequiredArgsConstructor
public class OfficeWebSocketHandler extends TextWebSocketHandler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConverter messageConverter;
    private final Cache<String, WebSocketSession> sessionCache;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        if (session.isOpen()) {
            sessionCache.put(session.getId(), session);
        }

        if (message.getPayload().equals("update")) {
            kafkaTemplate.sendDefault(messageConverter.toJsonFromMessage(new OfficeStateMessage()));
        }
    }

}
