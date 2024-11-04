package models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Route {

    private String planeName;
    private List<RouteDirection> directions = new ArrayList<>();

    public boolean isFree() {
        return planeName == null;
    }

}
