package org.romanzhula.office.controllers;

import lombok.RequiredArgsConstructor;
import models.Route;
import org.romanzhula.office.services.BufferForWaitingRoutesService;
import org.romanzhula.office.services.DirectionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routes")
public class RouteController {

    private final DirectionService directionService;
    private final BufferForWaitingRoutesService bufferForWaitingRoutesService;

    @PostMapping("/route")
    public void addRoute(
            @RequestBody List<String> routeLocations
    ) {
        Route route = directionService.convertLocationToRoute(routeLocations);
        bufferForWaitingRoutesService.addRoute(route);
    }

}
