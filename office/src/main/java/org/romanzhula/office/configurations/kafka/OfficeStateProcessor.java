package org.romanzhula.office.configurations.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.romanzhula.office.components.AirportProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import possible_messages.AirportStateMessage;
import possible_messages.OfficeStateMessage;
import utils.helpers.MessageConverter;
import utils.processors.MessageProcessor;

@Slf4j
@RequiredArgsConstructor
@Component("OFFICE_STATE") //(...) - key for Map processorsMap in MessageListener class
public class OfficeStateProcessor implements MessageProcessor<OfficeStateMessage> {

    private final MessageConverter messageConverter;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AirportProvider airportProvider;


    @Override
    public void processMessageFromJson(String jsonMessage) {
        airportProvider.getAirports().forEach(airport -> {
            kafkaTemplate.sendDefault(messageConverter.toJsonFromMessage(new AirportStateMessage(airport)));
        });
    }

}
