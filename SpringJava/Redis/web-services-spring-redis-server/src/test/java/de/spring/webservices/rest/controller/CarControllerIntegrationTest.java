package de.spring.webservices.rest.controller;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.spring.webservices.domain.Car;


/** WHEN USING @RunWith SPRING SEARCHES FOR YOUR main Aplication AND RUNS IT!!!!! **/
@RunWith(SpringRunner.class)
@WebMvcTest(CarController.class)
public class CarControllerIntegrationTest {
	
	// For injecting and mocking services which could be used in the Controller under test just use @MockBean and
	// then you can work with it using the traditional given willReturn statements from Mockito.
	//@MockBean
	//private CarService carService;
	
	@Inject
	private WebApplicationContext context;
	
	@Inject
	private ObjectMapper objectMapper;
	
	private MockMvc mockMvc;
	
    @Before
    public void setup() {        
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

            objectMapper
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule());
    }

	@Test
	public void testWhenGetAllCarsThenRetrieveJsonValues() throws Exception {
		mockMvc.perform(get("/api/cars/")
				.accept(MediaType.APPLICATION_JSON_UTF8))
		
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id", any(Integer.class)))
		.andExpect(jsonPath("$[0].content", is("Car: 1")))
		.andExpect(jsonPath("$[1].content", is("Car: 2")))
		.andExpect(jsonPath("$[1].id", any(Integer.class)))
		.andExpect(jsonPath("$[2].content", is("Car: 3")))
		.andExpect(jsonPath("$[2].id", any(Integer.class)))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	@Test
	public void testWhenGetOneCarThenRetrieveJsonValue() throws Exception {
		mockMvc.perform(get("/api/cars/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON_UTF8))
	
		.andExpect(status().isOk())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 1")))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	@Test
	public void testWhenCreateNewCarThenRetrieveJsonValue() throws Exception {
		Car car = new Car(2L, "nothing");
		mockMvc.perform(post("/api/cars/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(car))
				.accept(MediaType.APPLICATION_JSON_UTF8))
		
		.andExpect(status().isCreated())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 2")))
		.andExpect(header().string(HttpHeaders.LOCATION, "/api/cars/2"))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
}
