package de.spring.example.persistence.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.ClassRule;
import org.junit.Ignore;
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

// IT DOES NOT WORK FOR THESE REASONS:
// 1. Spring context is loaded before the DockerComposeRule
// 2. DockerComposeRule can work with random ports, the problem is:
// the Spring Context was loaded before DockerComposeRule and dataSource
// requires some port for connection to data base.

// These issues can be fixed using org.junit.Suite and running
// DockerComposeRule in the Suite instead of in every Test.
// But if I want to run just one test that solution does not work because
// DockerComposeRule will be declared in the Suite instead of in the Test.
// We will have to run our tests always from the Suite not being able to run
// just one Test from the IDE :(
@Ignore
public class AdDescriptionRepositoryDockerComposeRuleShould {
	
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
		
		for (AdDescription adDescription : adDescriptions) {
			assertThat(adDescription, is(notNullValue()));
		}
	}
	
}
