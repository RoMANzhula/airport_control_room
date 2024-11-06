package org.romanzhula.plane.schedulers;

import lombok.RequiredArgsConstructor;
import models.Plane;
import models.RouteDirection;
import org.romanzhula.plane.components.PlaneProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import possible_messages.PlaneStateMessage;
import utils.helpers.MessageConverter;

import java.util.List;
import java.util.Optional;

@EnableScheduling
@EnableAsync
@RequiredArgsConstructor
@Component
public class PlaneScheduler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageConverter messageConverter;

    private final PlaneProvider planeProvider;


    @Scheduled(initialDelay = 0, fixedDelay = 250) //check every 1/4 seconds
    public void flyingNow(){
        planeProvider.getPlanes().stream()
                .filter(Plane::hasRoute)
                .forEach(plane -> {
                    plane.getRoute()
                            .getDirections().stream()
                            .filter(RouteDirection::inProgress)
                            .findFirst()
                            .ifPresent(routeDirection -> {
                                plane.getPlanePosition(routeDirection);
                                routeDirection.addProgress(plane.getSpeed());

                                if (routeDirection.finishedProgress()) {
                                    plane.setLocation(routeDirection.getTo().getName());
                                }
                            })
                    ;
                })
        ;
    }

    @Async
    @Scheduled(initialDelay = 0, fixedDelay = 1000) //check every second
    public void notifyState() {
        planeProvider.getPlanes().stream()
                .filter(Plane::isFlying)
                .forEach(plane -> {
                    Optional<RouteDirection> routeDirection = plane
                            .getRoute()
                            .getDirections().stream()
                            .filter(RouteDirection::inProgress)
                            .findAny()
                    ;

                    if (routeDirection.isEmpty()) {
                        List<RouteDirection> routeDirections = plane.getRoute().getDirections();
                        plane.setLocation(routeDirections.get(routeDirections.size() - 1).getTo().getName());
                        plane.setFlying(false);
                    }

                    PlaneStateMessage planeStateMessage = new PlaneStateMessage(plane);
                    kafkaTemplate.sendDefault(messageConverter.toJsonFromMessage(planeStateMessage));
                })
        ;
    }

}
