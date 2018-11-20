package de.spring.webservices.rest;


import org.junit.Test;
import org.junit.runner.RunWith;
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
public class ApplicationIntegrationTest {
	@LocalServerPort
	private int port;

	private String url;


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
