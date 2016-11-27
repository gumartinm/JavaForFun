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
		assertThat(romanFor(2), is("II"));
		assertThat(romanFor(3), is("III"));
		// We jump to five because IV is an exception, it is
		// going to be more complicated and we do not already have
		// the pattern to extract the algorithm for the IV number.
		assertThat(romanFor(5), is("V"));
	}

}
