package de.aws.example.lambda;

public class Output {
	private String name;
	private String surname;
	private String rank;

	protected Output() {
		
	}
	
	public Output(String name, String surname, String rank) {
		this.name = name;
		this.surname = surname;
		this.rank = rank;
	}

	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public String getRank() {
		return rank;
	}
}
