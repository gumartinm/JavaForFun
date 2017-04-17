package de.aws.example.lambda;

public class Output {
	private String name;
	private String surname;
	
	protected Output() {
		
	}
	
	public Output(String name, String surname) {
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
