package de.example.tdd;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CalculatorTest {
	private Calculator calculator;
	
	@Before
	public void setUp() throws Exception {
		// Arrange
		calculator = new Calculator();
	}

	@Test
	public void testGivenTwoNumbersWhenAddingThenResult() {
		// Arrange
		
		
		// Act
		int result = calculator.sum(2, 2);
		
		// Assert
		assertEquals(4, result);
		
		
		// Act
		result = calculator.sum(8, 8);
		
		// Assert
		assertEquals(16, result);
	}
	
	@Test
	public void testGivenTwoNumbersWhenMultiplicationThenProduct() {


		assertEquals(16, calculator.product(4, 4));
		
		assertEquals(64, calculator.product(8, 8));
	}
}
