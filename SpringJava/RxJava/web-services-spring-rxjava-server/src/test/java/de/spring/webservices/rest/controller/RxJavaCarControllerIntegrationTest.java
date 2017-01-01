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

import javax.inject.Inject;

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
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.business.service.RxJavaBusinessLogic;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


// jsonPath, how to: https://github.com/jayway/JsonPath | http://jsonpath.herokuapp.com/ 

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:spring-configuration/mvc/rest/*.xml"})
public class RxJavaCarControllerIntegrationTest {
	private static final int PAGE = 2;
	private static final int PAGE_SIZE = 10;
	private static final String TEMPLATE = "Car: %s";
	
	@Inject
	private AsyncHandlerMethodReturnValueHandler singleReturnValueHandler;

	private RxJavaBusinessLogic rxJavaBusinessLogic;
	private RxJavaCarController controller;
	private MockMvc mockMvc;
	
    @Before
    public void setup() {
    	rxJavaBusinessLogic = mock(RxJavaBusinessLogic.class);
    	controller = new RxJavaCarController(rxJavaBusinessLogic);
        mockMvc = MockMvcBuilders
        		.standaloneSetup(controller)
        		.setCustomReturnValueHandlers(singleReturnValueHandler)
        		.build();
    }

	@Test
	public void testWhenGetAllCarsThenRetrieveJsonValues() throws Exception {
        final List<Car> cars = new ArrayList<>();
        cars.add(new Car(1L, String.format(TEMPLATE, 1)));        
        Observable<Page<Car>> observable = Observable
        		.create((Subscriber<? super Page<Car>> observer) -> {
        			observer.onNext( new PageImpl<>(cars));
        			observer.onCompleted();
        		}).subscribeOn(Schedulers.io());
		given(rxJavaBusinessLogic.findAll(new PageRequest(PAGE, PAGE_SIZE))).willReturn(observable);
		
		MvcResult result = mockMvc.perform(get("/api/rxjava/cars/")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(ResponseEntity.class)))
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
        Observable<Car> observable = Observable
        		.create((Subscriber<? super Car> observer) -> {
        			observer.onNext( new Car(1L, String.format(TEMPLATE, 1)));
        			observer.onCompleted();
        		}).subscribeOn(Schedulers.io());
		given(rxJavaBusinessLogic.findById(1L)).willReturn(observable);

		MvcResult result = mockMvc.perform(get("/api/rxjava/cars/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(ResponseEntity.class)))
				.andReturn();
	
		 mockMvc.perform(asyncDispatch(result))
		.andExpect(status().isOk())
		.andExpect(jsonPath("id", any(Integer.class)))
		.andExpect(jsonPath("content", is("Car: 1")))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	// THIS GUY IS USING MY de.spring.webservices.rest.controller.adapters.RxJavaAdapter AND IT DOES NOT NEED to call onCompleted()
	// I DO NOT THINK MY de.spring.webservices.rest.controller.adapters.RxJavaAdapter SHOULD BE USED. YOU'D BETTER USE THE spring netflix IMPLEMENTATION :/
	@Test
	public void testWhenCreateNewCarThenRetrieveJsonValue() throws Exception {
		Car car = new Car(null, "nothing");
        Observable<Car> observable = Observable.create(observer -> observer.onNext( new Car(1L, String.format(TEMPLATE, 1))));
		given(rxJavaBusinessLogic.createThrowable(car)).willReturn(observable);

		MvcResult result = mockMvc.perform(post("/api/rxjava/cars/")
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
		.andExpect(header().string(HttpHeaders.LOCATION, "/api/rxjava/cars/1"))
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
	}
	
	private static String asJsonString(final Object obj) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
}
