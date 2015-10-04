package de.example.equalshash;

import java.util.Objects;


public class Car {

	private final int id;
	private final String brand;
	
	public Car(int id, String brand) {
	    this.id = id;
	    this.brand = brand;
    }
	
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.brand);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Car other = (Car) obj;
	      
		return Objects.equals(this.id, other.id) &&
				Objects.equals(this.brand, other.brand);
	}
}
