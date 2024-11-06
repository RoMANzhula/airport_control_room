package org.romanzhula.plane.configurations.kafka_listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.romanzhula.plane.components.PlaneProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import possible_messages.OfficeStateMessage;
import possible_messages.PlaneStateMessage;
import utils.helpers.MessageConverter;
import utils.processors.MessageProcessor;

@Slf4j
@RequiredArgsConstructor
@Component("OFFICE_STATE")
public class OfficeStateProcessor implements MessageProcessor<OfficeStateMessage> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConverter messageConverter;
    private final PlaneProvider planeProvider;

    @Override
    public void processMessageFromJson(String jsonMessage) {
        planeProvider.getPlanes().forEach(plane -> {
            kafkaTemplate.sendDefault(messageConverter.toJsonFromMessage(new PlaneStateMessage(plane)));
        });
    }

}
