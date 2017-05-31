package de.example.spring.sqs.service.dto;

public class NotificationDTO {
	private String name;
	private String surname;
	
	protected NotificationDTO() {
		
	}
	
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
