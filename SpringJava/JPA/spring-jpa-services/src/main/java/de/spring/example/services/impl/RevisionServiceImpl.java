package de.spring.example.services.impl;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;

import de.spring.example.services.RevisionService;

public class RevisionServiceImpl<T, ID extends Serializable, N extends Number & Comparable<N>> implements RevisionService<T, ID, N> {

	@Override
	public Page<Revision<N, T>> findRevisions(Serializable id, Pageable pageable) {
		
		// TODO Auto-generated method stub
		return null;
	}

}
