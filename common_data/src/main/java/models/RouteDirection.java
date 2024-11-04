package models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RouteDirection {

    private RoutePoint from;
    private RoutePoint to;
    private double progress;

    public void addProgress(Double speed) {
        progress += speed;

        if (progress > 100) progress = 100D;
    }

    public Boolean finishedProgress() {
        return progress == 100D;
    }

    public Boolean inProgress() {
        return progress < 100D;
    }

}
