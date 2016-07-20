package de.spring.example.services;

import org.resthub.common.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.spring.example.persistence.domain.Ad;

public interface AdService extends CrudService<Ad, Long> {
	
	public Page<Ad> queryCriteriaExample(Pageable pageRequest);
}
