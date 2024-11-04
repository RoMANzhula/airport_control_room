package models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Airport {

    private String name;
    private List<String> planes = new ArrayList<>();
    private int x;
    private int y;

    private void addPlane(String planeName) {
        int index = planes.indexOf(planeName);

        if (index >= 0) {
            planes.set(index, planeName);
        } else {
            planes.add(planeName);
        }
    }

    private void removePlane(String planeName) {
        planes.remove(planeName);
    }

}
