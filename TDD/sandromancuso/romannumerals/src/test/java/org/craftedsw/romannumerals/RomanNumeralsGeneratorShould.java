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
		assertThat(romanFor(4), is("IV"));
		assertThat(romanFor(5), is("V"));
		assertThat(romanFor(7), is("VII"));
		assertThat(romanFor(10), is("X"));
		assertThat(romanFor(18), is("XVIII"));
		assertThat(romanFor(30), is("XXX"));
		assertThat(romanFor(2687), is("MMDCLXXXVII"));
		assertThat(romanFor(3499), is("MMMCDXCIX"));
	}

}
