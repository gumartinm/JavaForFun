package de.spring.example.persistence.repository;

import javax.inject.Inject;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

import de.spring.example.persistence.domain.AdDescription;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath*:spring-configuration/*.xml",
	"classpath*:spring-configuration-docker-test/*.xml"} )
@Transactional
public class AdDescriptionRepositoryShould {
	
	@Inject
	AdDescriptionRepository adDescriptionRepository;

	@ClassRule
    public static final DockerComposeRule DOCKER = DockerComposeRule.builder()
            .file("src/integTest/resources/docker/docker-compose.yml")
            .waitingForService("mysql", HealthChecks.toHaveAllPortsOpen())
            .saveLogsTo("build/dockerLogs")
            .build();
	
	@Test public void
	find_ad_descriptions_by_ad() {
		Iterable<AdDescription> adDescriptions = adDescriptionRepository.findAll();
		
		Iterable<AdDescription> lol = adDescriptions;
		
	}
	
}
