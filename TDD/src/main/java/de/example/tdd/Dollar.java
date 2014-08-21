package de.example.tdd;

public class Dollar {
	int amount;
	
	public Dollar(final int amount) {
		this.amount = amount;
	}
	
	public void times(final int multiplier) {
		amount *= multiplier;
	}
}
