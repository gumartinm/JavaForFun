package de.spring.webservices.rest;

public class Car {

    private final Long id;
    private final String content;

    // Required by Jackson :/
    public Car() {
    	this.id = null;
    	this.content = null;
    }
    
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
