package org.romanzhula.office.configurations.kafka;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import utils.helpers.MessageConverter;
import utils.processors.MessageProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component
public class MessageListener {

    private final MessageConverter messageConverter;
    private final Cache<String, WebSocketSession> sessionCache;

    @Autowired
    private Map<String, MessageProcessor<? extends Message>> processorsMap = new HashMap<>();

    @KafkaListener(id = "OfficeGroupId", topics = "office_routes")
    public void kafkaListen(String message) {
        sendKafkaMessageToSocket(message);
        String dataFromJson = messageConverter.extractDataFromJson(message);

        try {
            processorsMap.get(dataFromJson).processMessageFromJson(message);
        } catch (Exception e) {
            log.error("We have no input data: {}", e.getLocalizedMessage());
        }
    }

    private void sendKafkaMessageToSocket(String message) {
        sessionCache.asMap().values().forEach(webSocketSession -> {
            try {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }
        });
    }

}
