package org.romanzhula.office.configurations.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import possible_messages.OfficeRouteMessage;
import utils.processors.MessageProcessor;

@Slf4j
@RequiredArgsConstructor
@Component("OFFICE : ROUTE") // ключ для Map processorsMap у класі MessageListener
public class OfficeRouteProcessor implements MessageProcessor<OfficeRouteMessage> {

    @Override
    public void processMessageFromJson(String jsonMessage) {
        // Plug(gag): leave the processing logic empty, add logging
        log.info("Received OFFICE : ROUTE message, but processing is not required.");
    }
}
