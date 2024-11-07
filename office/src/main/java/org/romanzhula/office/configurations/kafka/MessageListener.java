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

    @KafkaListener(id = "OfficeGroupId", topics = "office-routes")
    public void kafkaListener(String message) {
        sendKafkaMessageToSocket(message);
        String dataFromJson = messageConverter.extractDataFromJson(message);

        try {
            MessageProcessor<? extends Message> processor = processorsMap.get(dataFromJson);

            if (processor != null) {
                processor.processMessageFromJson(message);
            } else {
                log.error("No processor found for key: {}", dataFromJson);
            }
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
