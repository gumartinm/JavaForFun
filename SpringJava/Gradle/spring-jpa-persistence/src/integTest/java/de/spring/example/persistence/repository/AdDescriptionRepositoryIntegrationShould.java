package de.spring.example.persistence.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import de.spring.example.persistence.domain.AdDescription;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath*:spring-configuration/*.xml",
	"classpath*:spring-configuration-docker-test/*.xml"} )
@Transactional
public class AdDescriptionRepositoryIntegrationShould {
	
	@Inject
	AdDescriptionRepository adDescriptionRepository;
	
	@Test public void
	find_ad_descriptions_by_ad() {
		Iterable<AdDescription> adDescriptions = adDescriptionRepository.findAll();
		
		for (AdDescription adDescription : adDescriptions) {
			assertThat(adDescription, is(notNullValue()));
		}
	}
	
}
