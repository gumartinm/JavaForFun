package de.spring.webservices.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import de.spring.webservices.rest.configuration.DatabaseConfiguration;
import de.spring.webservices.rest.configuration.DatabaseIntegrationTestConfiguration;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class,
		                    DatabaseConfiguration.class,
                            DatabaseIntegrationTestConfiguration.class },
                webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection = { "dbUnitLocations" })
public class ApplicationIntegrationTest {

	@LocalServerPort
	private int port;


	@Test
	@DatabaseSetup(connection = "dbUnitLocations",
	               value = { "/db/dbunit/locations/location_types.xml", "/db/dbunit/locations/locations.xml" })
	public void shouldFindAllLocations() {
		Response response = 
		        (Response) given()
		        		.port(port).accept(ContentType.JSON)
		        .when()
		        		.contentType(ContentType.JSON).get("/locations/")
		        .then()
		        	.assertThat().statusCode(HttpStatus.OK.value())
		        .extract();

		String description = from(response.body().asString()).getString("[1].description");
		assertThat(description, is("World"));
	}
}
