package de.example.spring.sqs.service.dto;

public class NotificationDTO {
	private final String name;
	private final String surname;
	
	public NotificationDTO(String name, String surname) {
		this.name = name;
		this.surname = surname;
	}
	
	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}
}
