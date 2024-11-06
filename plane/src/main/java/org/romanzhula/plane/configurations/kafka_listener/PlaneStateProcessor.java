package org.romanzhula.plane.configurations.kafka_listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import possible_messages.PlaneStateMessage;
import utils.processors.MessageProcessor;

@Slf4j
@Component("PLANE_STATE")
public class PlaneStateProcessor implements MessageProcessor<PlaneStateMessage> {

    @Override
    public void processMessageFromJson(String jsonMessage) {
        //Ignoring
    }

}
