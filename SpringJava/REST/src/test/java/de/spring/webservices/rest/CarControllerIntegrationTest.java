package de.spring.webservices.rest;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:spring-configuration/mvc/rest/*.xml"})
public class CarControllerIntegrationTest {
	private CarController controller;
	private MockMvc mockMvc;
	
    @Before
    public void setup() {
    	controller = new CarController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	public void testWhenGetAllCarsThenRetrieveJsonValues() throws Exception {
		mockMvc.perform(get("/api/cars/")
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
		
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id", any(Integer.class)))
		.andExpect(jsonPath("$[0].content", is("Car: 1")))
		.andExpect(jsonPath("$[1].content", is("Car: 2")))
		.andExpect(jsonPath("$[1].id", any(Integer.class)))
		.andExpect(jsonPath("$[2].content", is("Car: 3")))
		.andExpect(jsonPath("$[2].id", any(Integer.class)))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}
	
	@Test
	public void testWhenGetOneCarThenRetrieveJsonValue() throws Exception {
		mockMvc.perform(get("/api/cars/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
	
		.andExpect(status().isOk())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 1")))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}
	
	@Test
	public void testWhenCreateNewCarThenRetrieveJsonValue() throws Exception {
		Car car = new Car(2L, "nothing");
		mockMvc.perform(post("/api/cars/")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(asJsonString(car))
				.accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
		
		.andExpect(status().isCreated())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 1")))
		.andExpect(header().string(HttpHeaders.LOCATION, "/api/cars/1"))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}
	
	private static String asJsonString(final Object obj) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
}
