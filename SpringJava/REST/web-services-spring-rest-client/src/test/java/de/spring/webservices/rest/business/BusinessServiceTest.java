package de.spring.webservices.rest.business;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.spring.webservices.domain.Car;
import de.spring.webservices.rest.client.CarClientService;

public class BusinessServiceTest {

	private CarClientService carClientService;
	private BusinessService businessService;
	
    @Before
    public void createTest() {
    	carClientService = mock(CarClientService.class);
    	businessService = new BusinessService(carClientService);	
    }
    
	@Test
	public void whenDoSomethingWithCarsThenInvokeDoGetCars() {
		Car expectedOne = new Car(66L, "test");
		Car expectedTwo = new Car(99L, "example");
		List<Car> expected = new ArrayList<>();
		expected.add(expectedOne);
		expected.add(expectedTwo);
		when(carClientService.doGetCars()).thenReturn(expected);
		
		businessService.doSomethingWithCars();
		
		verify(carClientService, times(1)).doGetCars();
	}

    
	@Test
	public void whenDoSomethingWithOneCarhenInvokeDoGetCar() {
		Long id = 66L;
		Car expected = new Car(66L, "test");
		
		when(carClientService.doGetCar(id)).thenReturn(expected);
		
		businessService.doSomethingWithCar(id);
		
		verify(carClientService, times(1)).doGetCar(id);
	}
}
