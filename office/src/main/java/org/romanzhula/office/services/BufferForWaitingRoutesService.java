package org.romanzhula.office.services;

import lombok.RequiredArgsConstructor;
import models.Route;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class BufferForWaitingRoutesService {

    private final Lock lock = new ReentrantLock(true);
    private final List<Route> routes = new ArrayList<>();

    public List<Route> getListOfRoutes() {
        return routes;
    }

    public void addRoute(Route route) {
        lock.lock();

        try {
            routes.add(route);
        } finally {
            lock.unlock();
        }
    }

    public void removeRoute(Route route) {
        lock.lock();

        try {
            routes.removeIf(route::equals);
        } finally {
            lock.unlock();
        }
    }

}
