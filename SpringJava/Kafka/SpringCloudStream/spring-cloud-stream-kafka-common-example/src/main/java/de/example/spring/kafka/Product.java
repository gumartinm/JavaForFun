package de.example.spring.kafka;

public class Product {
	private String name;
	private String description;
	
	public Product(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
}
