package org.romanzhula.plane.configurations.kafka_listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import utils.helpers.MessageConverter;
import utils.processors.MessageProcessor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageListener {

    private final MessageConverter messageConverter;

    @Autowired
    private Map<String, MessageProcessor<? extends Message>> processorsMap = new HashMap<>();

    @KafkaListener(id = "PlaneGroupId", topics = "office-routes")
    public void locatorListener(String message) {
        String dataFromJson = messageConverter.extractDataFromJson(message);

        if (processorsMap != null) {
            try {
                processorsMap.get(dataFromJson).processMessageFromJson(message);
            } catch (Exception e) {
                log.error("Error processing message: {}", e.getLocalizedMessage());
            }
        } else {
            log.error("No processor found for data: {}", dataFromJson);
        }
    }

}