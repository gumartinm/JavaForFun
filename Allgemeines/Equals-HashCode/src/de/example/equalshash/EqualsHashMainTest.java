package de.example.equalshash;

import java.util.HashSet;


public class EqualsHashMainTest {

	public static void main(String[] args) {

		Car carA = new Car(1, "carA");
		Car carB = new Car(2, "carB");
		Car carC = new Car(3, "carC");
		Car carAA = new Car(1, "carA");
		
		HashSet<Car> cars = new HashSet<>();
		
		cars.add(null);
		cars.add(carA);
		cars.add(carB);
		
		if (cars.contains(carC)) {
			System.out.println("contains carC");
		} else {
			System.out.println("cars doesn't contain carC");
		}
		
		if (cars.contains(carAA)) {
			System.out.println("cars contains carAA");
		} else {
			System.out.println("cars doesn't contain carAA");
		}
		
		if (carA.equals(carAA)) {
			System.out.println("carA == carAA");
		} else {
			System.out.println("carA != carAA");
		}
		
		System.out.println("cars size before add carAA");
		System.out.println("cars size: " + cars.size());
		
		System.out.println("cars size after add carAA");
		cars.add(carAA);
		
		System.out.println("cars size: " + cars.size());
		
		
		/**
		 * without hashCode:
		 * 
		 * HashSet doesn't work
		 * 
		 */
		CarWithoutHashCode carWithoutHashCodeA = new CarWithoutHashCode(1, "carWithoutHashCodeA");
		CarWithoutHashCode carWithoutHashCodeB = new CarWithoutHashCode(2, "carWithoutHashCodeB");
		CarWithoutHashCode carWithoutHashCodeC = new CarWithoutHashCode(3, "carWithoutHashCodeC");
		CarWithoutHashCode carWithoutHashCodeAA = new CarWithoutHashCode(1, "carWithoutHashCodeA");
		
		HashSet<CarWithoutHashCode> carsWithoutHashCode = new HashSet<>();
		
		carsWithoutHashCode.add(null);
		carsWithoutHashCode.add(carWithoutHashCodeA);
		carsWithoutHashCode.add(carWithoutHashCodeB);
		
		if (carsWithoutHashCode.contains(carWithoutHashCodeC)) {
			System.out.println("carsWithoutHashCode contains carWithoutHashCodeC");
		} else {
			System.out.println("carsWithoutHashCode doesn't contain carWithoutHashCodeC");
		}
		
		if (carsWithoutHashCode.contains(carWithoutHashCodeAA)) {
			System.out.println("carsWithoutHashCode contains carWithoutHashCodeAA");
		} else {
			System.out.println("carsWithoutHashCode doesn't contain carWithoutHashCodeAA");
		}
		
		if (carWithoutHashCodeA.equals(carWithoutHashCodeAA)) {
			System.out.println("carWithoutHashCodeA == carWithoutHashCodeAA");
		} else {
			System.out.println("carWithoutHashCodeA != carWithoutHashCodeAA");
		}
		
		System.out.println("carsWithoutHashCode size before add carWithoutHashCodeAA");
		System.out.println("carsWithoutHashCode size: " + carsWithoutHashCode.size());
		
		System.out.println("carsWithoutHashCode size after add carWithoutHashCodeAA");
		carsWithoutHashCode.add(carWithoutHashCodeAA);
		
		System.out.println("carsWithoutHashCode size: " + carsWithoutHashCode.size());
		
		
		/**
		 * constant hashCode:
		 * 
		 * Works but there is no good distribution in the HashSet. It is like having an Array :(
		 * 
		 */
		CarConstantHashCode carConstantHashCodeA = new CarConstantHashCode(1, "carConstantHashCodeA");
		CarConstantHashCode carConstantHashCodeB = new CarConstantHashCode(2, "carConstantHashCodeB");
		CarConstantHashCode carConstantHashCodeC = new CarConstantHashCode(3, "carConstantHashCodeC");
		CarConstantHashCode carConstantHashCodeAA = new CarConstantHashCode(1, "carConstantHashCodeA");
		
		HashSet<CarConstantHashCode> carsConstantHashCode = new HashSet<>();
		
		carsConstantHashCode.add(null);
		carsConstantHashCode.add(carConstantHashCodeA);
		carsConstantHashCode.add(carConstantHashCodeB);
		
		if (carsConstantHashCode.contains(carConstantHashCodeC)) {
			System.out.println("carsConstantHashCode contains carConstantHashCodeC");
		} else {
			System.out.println("carsConstantHashCode doesn't contain carConstantHashCodeC");
		}
		
		if (carsConstantHashCode.contains(carConstantHashCodeAA)) {
			System.out.println("carsConstantHashCode contains carConstantHashCodeAA");
		} else {
			System.out.println("carsConstantHashCode doesn't contain carConstantHashCodeAA");
		}
		
		if (carConstantHashCodeA.equals(carConstantHashCodeAA)) {
			System.out.println("carConstantHashCodeA == carConstantHashCodeAA");
		} else {
			System.out.println("carConstantHashCodeA != carConstantHashCodeAA");
		}
		
		System.out.println("carsConstantHashCode size before add carConstantHashCodeAA");
		System.out.println("carsConstantHashCode size: " + carsConstantHashCode.size());
		
		carsConstantHashCode.add(carConstantHashCodeAA);
		
		System.out.println("carsConstantHashCode size after add carConstantHashCodeAA");
		System.out.println("carsConstantHashCode size: " + carsConstantHashCode.size());
		
		
		
		/**
		 * without equals:
		 * 
		 * HashSet doesn't work
		 * 
		 */
		CarWithoutEquals carWithoutEqualsA = new CarWithoutEquals(1, "carWithoutEqualsA");
		CarWithoutEquals carWithoutEqualsB = new CarWithoutEquals(2, "carWithoutEqualsB");
		CarWithoutEquals carWithoutEqualsC = new CarWithoutEquals(3, "carWithoutEqualsC");
		CarWithoutEquals carWithoutEqualsAA = new CarWithoutEquals(1, "carWithoutEqualsA");
		
		HashSet<CarWithoutEquals> carsWithoutEquals = new HashSet<>();
		
		carsWithoutEquals.add(null);
		carsWithoutEquals.add(carWithoutEqualsA);
		carsWithoutEquals.add(carWithoutEqualsB);
		
		if (carsWithoutEquals.contains(carWithoutEqualsC)) {
			System.out.println("carsWithoutEquals contains carWithoutEqualsC");
		} else {
			System.out.println("carsWithoutEquals doesn't contain carWithoutEqualsC");
		}
		
		if (carsWithoutEquals.contains(carWithoutEqualsAA)) {
			System.out.println("carsWithoutEquals contains carWithoutEqualsAA");
		} else {
			System.out.println("carsWithoutEquals doesn't contain carWithoutEqualsAA");
		}
		
		if (carWithoutEqualsA.equals(carWithoutEqualsAA)) {
			System.out.println("carWithoutEqualsA == carWithoutEqualsAA");
		} else {
			System.out.println("carWithoutEqualsA != carWithoutEqualsAA");
		}
		
		System.out.println("carsWithoutEquals size before add carWithoutEqualsAA");
		System.out.println("carsWithoutEquals size: " + carsWithoutEquals.size());
		
		carsWithoutEquals.add(carWithoutEqualsAA);
		
		System.out.println("carsWithoutEquals size after add carWithoutEqualsAA");
		System.out.println("carsWithoutEquals size: " + carsWithoutEquals.size());
	}
}
