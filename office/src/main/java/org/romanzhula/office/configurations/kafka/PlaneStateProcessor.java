package org.romanzhula.office.configurations.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.Airport;
import models.Plane;
import models.Route;
import org.romanzhula.office.components.AirportProvider;
import org.romanzhula.office.components.PlaneProvider;
import org.romanzhula.office.services.BufferForWaitingRoutesService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import possible_messages.AirportStateMessage;
import possible_messages.PlaneStateMessage;
import utils.helpers.MessageConverter;
import utils.processors.MessageProcessor;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Component("PLANE : STATE") //key for Map processorsMap in MessageListener class
public class PlaneStateProcessor implements MessageProcessor<PlaneStateMessage> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConverter messageConverter;

    private final PlaneProvider planeProvider;
    private final AirportProvider airportProvider;
    private final BufferForWaitingRoutesService bufferForWaitingRoutesService;


    @Override
    public void processMessageFromJson(String jsonMessage) {
        PlaneStateMessage planeStateMessage =
                messageConverter.extractMessageFromJson(jsonMessage, PlaneStateMessage.class)
        ;

        Plane plane = planeStateMessage.getPlane();
        Optional<Plane> previousPlaneOptional = planeProvider.getPlane(plane.getName());
        Airport airport = airportProvider.getAirport(plane.getLocation());

        planeProvider.addPlane(plane);

        if (previousPlaneOptional.isPresent() && plane.hasRoute() && !previousPlaneOptional.get().hasRoute()) {
            Route route = plane.getRoute();
            bufferForWaitingRoutesService.removeRoute(route);
        }

        if (previousPlaneOptional.isEmpty() || !plane.isFlying() && previousPlaneOptional.get().isFlying()) {
            airport.addPlane(plane.getName());

            kafkaTemplate.sendDefault(messageConverter.toJsonFromMessage(new AirportStateMessage(airport)));
        }
    }

}