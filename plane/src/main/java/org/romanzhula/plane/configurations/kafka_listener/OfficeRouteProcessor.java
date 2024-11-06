package org.romanzhula.plane.configurations.kafka_listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.Route;
import org.romanzhula.plane.components.PlaneProvider;
import org.springframework.stereotype.Component;
import possible_messages.OfficeRouteMessage;
import utils.helpers.MessageConverter;
import utils.processors.MessageProcessor;

@Slf4j
@RequiredArgsConstructor
@Component("OFFICE_ROUTE")
public class OfficeRouteProcessor implements MessageProcessor<OfficeRouteMessage> {

    private final MessageConverter messageConverter;
    private final PlaneProvider planeProvider;

    @Override
    public void processMessageFromJson(String jsonMessage) {
        OfficeRouteMessage officeRouteMessage =
                messageConverter.extractMessageFromJson(jsonMessage, OfficeRouteMessage.class)
        ;

        Route route = officeRouteMessage.getRoute();

        planeProvider.getPlanes().stream()
                .filter(plane -> plane.noFlying() && route.getPlaneName().equals(plane.getName()))
                .findFirst()
                .ifPresent(plane -> {
                    plane.setRoute(route);
                    plane.setFlying(true);
                    plane.setLocation(null);
                })
        ;
    }

}
