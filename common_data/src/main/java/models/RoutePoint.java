package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoutePoint {

    private String name;
    private double xCoordinate;
    private double yCoordinate;

    public RoutePoint(Airport airport) {
        this.name = airport.getName();
        this.xCoordinate = airport.getX();
        this.yCoordinate = airport.getY();
    }

}
