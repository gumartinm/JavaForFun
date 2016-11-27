package org.craftedsw.romannumerals;

import static org.craftedsw.romannumerals.RomanNumeralGenerator.romanFor;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RomanNumeralsGeneratorShould {

	@Test public void
	generate_roman_numeral_for_a_given_decimal_number() {
		
		
		
		// You must always start from the assert!!!
		// Method's name must describe what the assert is doing!!!
		assertThat(romanFor(1), is("I"));
	}

}
