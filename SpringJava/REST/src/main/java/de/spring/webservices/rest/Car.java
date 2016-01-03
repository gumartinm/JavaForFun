package de.spring.webservices.rest;

public class Car {

    private final Long id;
    private final String content;

    public Car(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
