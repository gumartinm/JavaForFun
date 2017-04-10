package de.spring.webservices.rest.controller.dto;

public class NotificationDTO {
	private final String name;
	private final String surname;
	private final String message;
	
	public NotificationDTO(String name, String surname, String message) {
		this.name = name;
		this.surname = surname;
		this.message = message;
	}
	
	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getMessage() {
		return message;
	}
}
