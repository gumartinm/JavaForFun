package de.spring.webservices.rest.controller;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.business.service.AwesomeBusinessLogic;


// jsonPath, how to: https://github.com/jayway/JsonPath | http://jsonpath.herokuapp.com/ 

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:spring-configuration/mvc/rest/*.xml"})
public class CarControllerIntegrationTest {
	private static final int PAGE = 2;
	private static final int PAGE_SIZE = 10;
	private static final String TEMPLATE = "Car: %s";
	
	private AwesomeBusinessLogic awesomeBusinessLogic;
	private CarController controller;
	private MockMvc mockMvc;
	
    @Before
    public void setup() {
    	awesomeBusinessLogic = mock(AwesomeBusinessLogic.class);
    	controller = new CarController(awesomeBusinessLogic);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	public void testWhenGetAllCarsThenRetrieveJsonValues() throws Exception {
        final List<Car> cars = new ArrayList<>();
        cars.add(new Car(1L, String.format(TEMPLATE, 1)));
		given(awesomeBusinessLogic.findAll(new PageRequest(PAGE, PAGE_SIZE))).willReturn(new PageImpl<>(cars));
		
		mockMvc.perform(get("/api/cars/")
				.accept(MediaType.APPLICATION_JSON_UTF8))
		
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content[0].id", any(Integer.class)))
		.andExpect(jsonPath("$.content[0].content", is("Car: 1")))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	@Test
	public void testWhenGetOneCarThenRetrieveJsonValue() throws Exception {
		given(awesomeBusinessLogic.findById(1L)).willReturn(new Car(1L, String.format(TEMPLATE, 1)));

		mockMvc.perform(get("/api/cars/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON_UTF8))
	
		.andExpect(status().isOk())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 1")))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	@Test
	public void testWhenCreateNewCarThenRetrieveJsonValue() throws Exception {
		Car car = new Car(null, "nothing");
		given(awesomeBusinessLogic.create(car)).willReturn(new Car(1L, String.format(TEMPLATE, 1)));

		mockMvc.perform(post("/api/cars/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(asJsonString(car))
				.accept(MediaType.APPLICATION_JSON_UTF8))
		
		.andExpect(status().isCreated())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 1")))
		.andExpect(header().string(HttpHeaders.LOCATION, "/api/cars/1"))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	private static String asJsonString(final Object obj) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
}
