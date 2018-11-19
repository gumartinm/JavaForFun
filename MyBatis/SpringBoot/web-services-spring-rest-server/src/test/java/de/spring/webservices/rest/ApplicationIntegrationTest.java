package de.spring.webservices.rest;


import javax.inject.Inject;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import de.spring.webservices.rest.configuration.DatabaseConfiguration;
import de.spring.webservices.rest.configuration.DatabaseIntegrationTestConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class, DatabaseConfiguration.class,
        DatabaseIntegrationTestConfiguration.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@Ignore
public class ApplicationIntegrationTest {

	@Qualifier(DatabaseConfiguration.FLYWAY_LOCATIONS)
	@Inject
	private Flyway flywayLocations;

	@Qualifier(DatabaseConfiguration.FLYWAY_CONCILIATION)
	@Inject
	private Flyway flywayConciliation;

	@LocalServerPort
	private int port;

	private String url;


	@Before
	public void setUp() {
		url = String.format("http://localhost:%d", port);

		flywayLocations.migrate();
		flywayConciliation.migrate();
	}

	@After
	public void tearDown() {
		flywayLocations.clean();
		flywayConciliation.clean();
	}

	@Test
	public void shouldFindAllLocations() {
		RestAssured.given().port(port).accept(ContentType.JSON).when().contentType(ContentType.JSON)
		        .get(url + "/locations/").then().assertThat().statusCode(HttpStatus.OK.value());
		// given().port(port).accept(ContentType.JSON).when().contentType(ContentType.JSON).get(url
		// + "/health").then()
		// .assertThat().statusCode(HttpStatus.OK.value()).body("$", hasKey("status"))
		// .body("status", containsString("UP"));
	}
}
