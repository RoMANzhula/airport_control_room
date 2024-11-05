package org.romanzhula.office.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.Airport;
import models.Plane;
import models.Route;
import models.RouteDirection;
import org.romanzhula.office.components.AirportProvider;
import org.romanzhula.office.components.PlaneProvider;
import org.romanzhula.office.services.BufferForWaitingRoutesService;
import org.romanzhula.office.services.DirectionService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import possible_messages.AirportStateMessage;
import possible_messages.OfficeRouteMessage;
import utils.helpers.MessageConverter;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Component
public class RouteDistributeScheduler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConverter messageConverter;

    private final DirectionService directionService;
    private final PlaneProvider planeProvider;
    private final BufferForWaitingRoutesService bufferForWaitingRoutesService;
    private final AirportProvider airportProvider;

    //start after run app after 0,5 second, check every 2,5 seconds
    @Scheduled(initialDelay = 500, fixedDelay = 2500)
    private void distribute() {
        bufferForWaitingRoutesService.getListOfRoutes().stream()
                .filter(Route::isFree) //only free routes
                .forEach(route -> {
                    String startLocation = route.getDirections().get(0).getFrom().getName();

                    planeProvider.getPlanes().stream()
                            .filter(plane -> startLocation.equals(plane.getLocation()) && plane.noFlying())
                            .findFirst()
                            .ifPresent(plane -> sendPlaneToRoute(route, plane))
                    ;

                    if (route.isFree()) {
                        planeProvider.getPlanes().stream()
                                .filter(Plane::noFlying)
                                .findFirst()
                                .ifPresent(plane -> {
                                    String currentLocation = plane.getLocation();
                                    if (!currentLocation.equals(startLocation)) {
                                        RouteDirection routeDirection = directionService.makeDirection(
                                                currentLocation,
                                                startLocation
                                        );

                                        //set to begin of list
                                        route.getDirections().add(0, routeDirection);
                                    }

                                    sendPlaneToRoute(route, plane);
                                })
                        ;
                    }
                })
        ;
    }

    //ties the plane to the route
    private void sendPlaneToRoute(Route route, Plane plane) {
        route.setPlaneName(plane.getName());

        Airport airport = airportProvider.findAirportAndRemovePlane(plane.getName());

        plane.setLocation(null);

        //send updated data
        kafkaTemplate.sendDefault(messageConverter.toJsonFromMessage(new OfficeRouteMessage(route)));
        kafkaTemplate.sendDefault(messageConverter.toJsonFromMessage(new AirportStateMessage(airport)));
    }

}
