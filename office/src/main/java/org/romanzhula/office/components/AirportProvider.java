package org.romanzhula.office.components;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.Airport;
import models.RoutePoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
@Getter
@ConfigurationProperties(prefix = "application")
public class AirportProvider {

    private final List<Airport> airports = new ArrayList<>();

    public Airport findAirportAndRemovePlane(String planeName) {
        AtomicReference<Airport> result = new AtomicReference<>();

        airports.stream()
                .filter(airport -> airport.getPlanes().contains(planeName))
                .findFirst()
                .ifPresent(airport -> {
                    airport.removePlane(planeName);
                    result.set(airport);
                })
        ;

        return result.get(); // null or found airport
    }

    public RoutePoint getRoutePoint(String airportName) {
        return new RoutePoint(getAirport(airportName));
    }

    public Airport getAirport(String airportName) {
        return airports.stream()
                .filter(airport -> airport.getName().equals(airportName))
                .findFirst()
                .orElse(new Airport())
        ;
    }

}
