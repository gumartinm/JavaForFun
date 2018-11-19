package de.spring.webservices.rest.persistence.repository;

import java.util.List;

import de.spring.webservices.domain.LocationType;

public interface LocationTypeRepository extends LocationsBaseRepository {

	void save(LocationType locationType);

	List<LocationType> findAll();
}
