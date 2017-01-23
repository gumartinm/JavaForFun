package de.spring.example.services.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;

import de.spring.example.persistence.domain.AdDescription;
import de.spring.example.persistence.repository.AdDescriptionRepository;

public class AdDescriptionRevisionServiceShould {

	@Test
	public void test() {
		AdDescriptionRepository adDescriptionRepository = mock(AdDescriptionRepository.class);
		AdDescriptionRevisionServiceImpl adDescriptionRevisionService = new AdDescriptionRevisionServiceImpl();
		adDescriptionRevisionService.setRepository(adDescriptionRepository);
		List<Revision<Integer, AdDescription>> adRevisions = new ArrayList<>();
		Pageable pageRequest = new PageRequest(0, 1);
	    Page<Revision<Integer, AdDescription>> page = new PageImpl<>(adRevisions);

		given(adDescriptionRepository.findRevisions(1L, pageRequest)).willReturn(page);
		
	    assertThat(adDescriptionRevisionService.findRevisions(1L, pageRequest), is(page));
	}

}
