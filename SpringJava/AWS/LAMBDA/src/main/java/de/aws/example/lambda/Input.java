package de.aws.example.lambda;

public class Input {
	private String name;
	
	protected Input() {
		
	}
	
	public Input(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
