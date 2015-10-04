package de.example.equalshash;

import java.util.Objects;


public class CarConstantHashCode {

	private final int id;
	private final String brand;
	
	public CarConstantHashCode(int id, String brand) {
	    this.id = id;
	    this.brand = brand;
    }
	
	@Override
	public int hashCode() {
		return 666;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CarConstantHashCode other = (CarConstantHashCode) obj;
	      
		return Objects.equals(this.id, other.id) &&
				Objects.equals(this.brand, other.brand);
	}
}
