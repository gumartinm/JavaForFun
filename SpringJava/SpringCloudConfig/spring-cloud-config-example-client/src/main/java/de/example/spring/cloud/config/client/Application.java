package de.example.spring.cloud.config.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {
    @Value("${user.name}")
    private String name;

    @Value("${user.surname}")
    private String surname;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/username")
    public String findOne() {
        return String.format("Cobra files. Name: %s / Surname: '%s'", name, surname);
    }
}
