package de.spring.webservices.rest.controller;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.spring.webservices.domain.Location;
import de.spring.webservices.rest.service.LocationService;


/** WHEN USING @RunWith SPRING SEARCHES FOR YOUR main Aplication AND RUNS IT!!!!! **/
@RunWith(SpringRunner.class)
@WebMvcTest(Location.class)
@Ignore
public class LocationControllerIntegrationTest {
	
	// For injecting and mocking services which could be used in the Controller under test just use @MockBean and
	// then you can work with it using the traditional given willReturn statements from Mockito.
	//@MockBean
    private LocationService locationService;
	
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
}
