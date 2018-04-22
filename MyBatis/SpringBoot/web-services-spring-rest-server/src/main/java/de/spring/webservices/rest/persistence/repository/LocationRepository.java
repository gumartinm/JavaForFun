package de.spring.webservices.rest.persistence.repository;

import java.util.List;

import de.spring.webservices.domain.Location;

public interface LocationRepository extends BaseRepository {

    List<Location> findAll();

    long save(Location location);

}
