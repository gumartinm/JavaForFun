package de.spring.webservices.rest.persistence.repository.conciliation;

import java.util.List;

import de.spring.webservices.domain.conciliation.Layer;

public interface ConciliationRepository extends ConciliationBaseRepository {

	List<Layer> findAll();
}
