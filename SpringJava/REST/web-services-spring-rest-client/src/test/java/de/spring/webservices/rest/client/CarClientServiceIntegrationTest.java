package de.spring.webservices.rest.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.spring.webservices.domain.Car;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring-configuration/rest-config.xml")
public class CarClientServiceIntegrationTest {
	
	@Value("${url.base}${url.cars}")
	private String apiCarsUrl;
	
	@Value("${url.base}${url.car}")
	private String apiCarUrl;
	
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
	private CarClientService carClientService;

    private MockRestServiceServer mockServer;

    @Before
    public void createTest() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

	@Test
	public void whenGetAllCarsThenRetrieveRequestedCars() throws JsonProcessingException {
		Car expectedOne = new Car(66L, "test");
		List<Car> expected = new ArrayList<>();
		expected.add(expectedOne);
		
		mockServer.expect(requestTo(apiCarsUrl))
					.andExpect(method(HttpMethod.GET))
					.andRespond(withSuccess(asJsonString(expected), MediaType.APPLICATION_JSON_UTF8));

		List<Car> cars = carClientService.doGetCars();

		mockServer.verify();
		
		assertEquals(1, cars.size());
		assertEquals(expectedOne, cars.get(0));
	}
	
	@Test
	public void whenGetCarByIdThenRetrieveRequestedCar() throws JsonProcessingException {
		Long id = 66L;
		Car expected = new Car(66L, "test");
		
		mockServer.expect(requestTo(apiCarUrl.replace(":id", String.valueOf(id))))
					.andExpect(method(HttpMethod.GET))
					.andRespond(withSuccess(asJsonString(expected), MediaType.APPLICATION_JSON_UTF8));

		Car car = carClientService.doGetCar(id);

		mockServer.verify();
		
		assertNotNull(car);
		assertEquals(expected, car);
	}
	
	@Test
	public void whenCreateNewCarThenRetrieveCreatedCar() throws JsonProcessingException {
		Long expectedId = 66L;
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.LOCATION, "/api/cars/" + String.valueOf(expectedId));
		Car expected = new Car(expectedId, "test");
		
		mockServer.expect(requestTo(apiCarsUrl))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess(asJsonString(expected), MediaType.APPLICATION_JSON_UTF8)
							.headers(headers));

		Car car = carClientService.doNewCar(expected);

		mockServer.verify();
		
		assertNotNull(car);
		assertEquals(expected, car);
	}
	
	private static String asJsonString(final Object obj) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
}
