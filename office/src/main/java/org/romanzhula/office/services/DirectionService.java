package org.romanzhula.office.services;

import lombok.RequiredArgsConstructor;
import models.Route;
import models.RouteDirection;
import models.RoutePoint;
import org.romanzhula.office.components.AirportProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectionService {

    private final AirportProvider airportProvider;

    public RouteDirection makeDirection(String from, String to) {
        return new RouteDirection(airportProvider.getRoutePoint(from), airportProvider.getRoutePoint(to), 0);
    }

    public Route convertLocationToRoute(List<String> locations) {
        Route route = new Route();

        List<RouteDirection> routeDirections = new ArrayList<>();
        List<RoutePoint> routePoints = new ArrayList<>();

        locations.forEach(location -> {
            airportProvider.getAirports().stream()
                    .filter(airport -> airport.getName().equals(location))
                    .findFirst()
                    .ifPresent(airport -> routePoints.add(new RoutePoint(airport)))
            ;
        });

        for (int i = 0; i < routePoints.size() - 1; i++) {
            routeDirections.add(new RouteDirection());
        }

        routeDirections.forEach(direction -> {
            int index = routeDirections.indexOf(direction);

            if (direction.getFrom() == null) {
                direction.setFrom(routePoints.get(index));
                if (routePoints.size() > index + 1) {
                    direction.setTo(routePoints.get(index + 1));
                } else {
                    direction.setTo(routePoints.get(index));
                }
            }
        });

        route.setDirections(routeDirections);

        return route;
    }

}
