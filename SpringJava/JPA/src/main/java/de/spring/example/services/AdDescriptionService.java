package de.spring.example.services;

import org.resthub.common.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.spring.example.persistence.domain.AdDescription;

public interface AdDescriptionService extends CrudService<AdDescription, Long> {

	public Page<AdDescription> queryDslExample(Pageable pageRequest);
}
