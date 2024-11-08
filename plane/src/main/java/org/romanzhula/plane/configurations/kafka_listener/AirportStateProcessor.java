package org.romanzhula.plane.configurations.kafka_listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import possible_messages.AirportStateMessage;
import utils.processors.MessageProcessor;

@Slf4j
@RequiredArgsConstructor
@Component("AIRPORT : STATE") //(...) - key for Map processorsMap in MessageListener class
public class AirportStateProcessor implements MessageProcessor<AirportStateMessage> {

    @Override
    public void processMessageFromJson(String jsonMessage) {

        log.info("Received AIRPORT : STATE message, but processing is not required.");
    }

}
