package models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Plane {

    private String name;
    private String location;
    private Route route;
    private boolean flying;
    private double speed;
    private double xCoordinate;
    private double yCoordinate;
    private double angleOfBoard;

    private Boolean noFlying() {
        return !flying;
    }

    private void getPlanePosition(RouteDirection routeDirection) {
        //formula Bezier Curve B(t) = (1 - t)P0 + tP1  t є [0,1]
        double t = routeDirection.getProgress() / 100; // from 0 to 1

        double toXCoordinate =
                (1 - t) * routeDirection.getFrom().getXCoordinate() + t * routeDirection.getTo().getXCoordinate()
        ;

        double toYCoordinate =
                (1 - t) * routeDirection.getFrom().getYCoordinate() + t * routeDirection.getTo().getYCoordinate()
        ;

        // for calculate turning angel
        double deltaXCoordinate = this.xCoordinate - toXCoordinate;
        double deltaYCoordinate = this.yCoordinate - toYCoordinate;
        this.angleOfBoard = Math.toDegrees(Math.atan2(deltaYCoordinate, deltaXCoordinate));

        if (this.angleOfBoard < 0) this.angleOfBoard = 360 + this.angleOfBoard;

        this.xCoordinate = toXCoordinate;
        this.yCoordinate = toYCoordinate;

    }

}
