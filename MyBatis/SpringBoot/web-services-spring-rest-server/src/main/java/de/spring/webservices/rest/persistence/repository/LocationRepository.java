package de.spring.webservices.rest.persistence.repository;

import java.util.List;

import de.spring.webservices.domain.Location;
import de.spring.webservices.domain.Location.Point;

public interface LocationRepository extends BaseRepository {

    List<Location> findAll();

    void save(Location location);

    List<Location> findAllByPointAndRadius(Point point, double radius);
}
