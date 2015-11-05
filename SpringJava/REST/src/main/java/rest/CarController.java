package rest;

import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cars/")
public class CarController {

    private static final String template = "Car: %s";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(produces = { "application/json" }, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Car> cars() {
        final List<Car> cars = new ArrayList<>();
        cars.add(new Car(counter.incrementAndGet(), String.format(template, 1)));
        cars.add(new Car(counter.incrementAndGet(), String.format(template, 2)));
        cars.add(new Car(counter.incrementAndGet(), String.format(template, 3)));

        return cars;
    }

    @RequestMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Car car(@RequestHeader(value = "MY_HEADER", required = false) String specialHeader,
    		@PathVariable("id") long id,
    		@RequestParam Map<String, String> params,
    		@RequestParam(value = "wheel", required = false) String[] wheelParams) {
    	    	
    	if (specialHeader != null) {
    		System.out.println("SPECIAL HEADER: " + specialHeader);
    	}
    	 
    	if (params.get("mirror") != null) {
    		System.out.println("MIRROR: " + params.get("mirror"));	
    	}
    	
    	if (params.get("window") != null) {
    		System.out.println("WINDOW: " + params.get("window"));
    	}
    	
    	if (wheelParams != null) {
    		for(String wheel : wheelParams) {
    			System.out.println(wheel);
    		}
    	}
    	
        try {
            Thread.sleep(10000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }


        return new Car(counter.incrementAndGet(), String.format(template, id));
    }
}
