package org.romanzhula.office.components;

import lombok.Getter;
import models.Plane;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class PlaneProvider {

    @Getter
    private final List<Plane> planes = new ArrayList<>();
    //to maintain a message queue
    private final Lock lock = new ReentrantLock(true);


    public Optional<Plane> getPlane(String planeName) {
        return planes.stream()
                .filter(plane -> plane.getName().equals(planeName))
                .findFirst()
        ;
    }

    public void addPlane(Plane plane) {
        Optional<Plane> optionalPlane = getPlane(plane.getName());

        lock.lock();

        try {
            if (optionalPlane.isPresent()) {
                int index = planes.indexOf(optionalPlane.get());

                planes.set(index, plane);
            } else {
                planes.add(plane);
            }
        } finally {
            lock.unlock();
        }
    }

}
