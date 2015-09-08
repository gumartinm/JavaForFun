package rest;

import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cars")
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

    @RequestMapping(value = "/{id}", produces = { "application/json" }, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Car car(@PathVariable("id") long id) {
        try {
            Thread.sleep(10000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }


        return new Car(counter.incrementAndGet(), String.format(template, id));
    }
}
