package de.spring.webservices.rest.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.business.service.CompletableFutureBusinessLogic;


// jsonPath, how to: https://github.com/jayway/JsonPath | http://jsonpath.herokuapp.com/ 

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:spring-configuration/mvc/rest/*.xml"})
public class CompletableFutureCarControllerIntegrationTest {
	private static final int PAGE = 2;
	private static final int PAGE_SIZE = 10;
	private static final String TEMPLATE = "Car: %s";
	
	private CompletableFutureBusinessLogic completableFutureBusinessLogic;
	private CompletableFutureCarController controller;
	private MockMvc mockMvc;
	
    @Before
    public void setup() {
    	completableFutureBusinessLogic = mock(CompletableFutureBusinessLogic.class);
    	controller = new CompletableFutureCarController(completableFutureBusinessLogic);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	public void testWhenGetAllCarsThenRetrieveJsonValues() throws Exception {
        final List<Car> cars = new ArrayList<>();
        cars.add(new Car(1L, String.format(TEMPLATE, 1)));
        CompletableFuture<Page<Car>> future = CompletableFuture.supplyAsync(() -> new PageImpl<>(cars));
		given(completableFutureBusinessLogic.findAll(new PageRequest(PAGE, PAGE_SIZE))).willReturn(future);
		
		MvcResult result = mockMvc.perform(get("/api/completablefuture/cars/")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(Page.class)))
				.andReturn();
		
		 mockMvc.perform(asyncDispatch(result))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content[0].id", any(Integer.class)))
		.andExpect(jsonPath("$.content[0].id", any(Integer.class)))
		.andExpect(jsonPath("$.content[0].content", is("Car: 1")))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	@Test
	public void testWhenGetOneCarThenRetrieveJsonValue() throws Exception {
		CompletableFuture<Car> expected = CompletableFuture.supplyAsync(() -> new Car(1L, String.format(TEMPLATE, 1)));
		given(completableFutureBusinessLogic.findById(1L)).willReturn(expected);

		MvcResult result = mockMvc.perform(get("/api/completablefuture/cars/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(Car.class)))
				.andReturn();
	
		 mockMvc.perform(asyncDispatch(result))
		.andExpect(status().isOk())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 1")))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	@Test
	public void testWhenCreateNewCarThenRetrieveJsonValue() throws Exception {
		Car car = new Car(null, "nothing");
		CompletableFuture<Car> expected = CompletableFuture.supplyAsync(() -> new Car(1L, String.format(TEMPLATE, 1)));
		given(completableFutureBusinessLogic.createThrowable(car)).willReturn(expected);

		MvcResult result = mockMvc.perform(post("/api/completablefuture/cars/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(asJsonString(car))
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(ResponseEntity.class)))
				.andReturn();
		
		mockMvc.perform(asyncDispatch(result))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 1")))
		.andExpect(header().string(HttpHeaders.LOCATION, "/api/completablefuture/cars/1"))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	private static String asJsonString(final Object obj) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
}
