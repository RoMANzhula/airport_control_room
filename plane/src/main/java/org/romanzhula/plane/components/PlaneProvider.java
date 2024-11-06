package org.romanzhula.plane.components;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import models.Plane;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
@ConfigurationProperties(prefix = "application")
@Component
public class PlaneProvider {

    private final List<Plane> planes = new ArrayList<>();

}
