package de.example.equalshash;

import java.util.Objects;


public class CarWithoutEquals {

	private final int id;
	private final String brand;
	
	public CarWithoutEquals(int id, String brand) {
	    this.id = id;
	    this.brand = brand;
    }
	
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.brand);
	}
	
	/**
	 * If not implementing equals it will be used the one implemented by Object class.
	 * The equals implemented by Object class depends on the instance pointer.
	 */
}
