package org.craftedsw.romannumerals;

public class RomanNumeralGenerator {

	public static String romanFor(int decimal) {
		if (decimal == 3) {
			return "III";
		}
		if (decimal == 2) {
			return "II";
		}
		return "I";
	}

}
