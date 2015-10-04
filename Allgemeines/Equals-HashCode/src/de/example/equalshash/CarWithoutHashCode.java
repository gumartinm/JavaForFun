package de.example.equalshash;

import java.util.Objects;


public class CarWithoutHashCode {

	private final int id;
	private final String brand;
	
	public CarWithoutHashCode(int id, String brand) {
	    this.id = id;
	    this.brand = brand;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CarWithoutHashCode other = (CarWithoutHashCode) obj;
	      
		return Objects.equals(this.id, other.id) &&
				Objects.equals(this.brand, other.brand);
	}
}
