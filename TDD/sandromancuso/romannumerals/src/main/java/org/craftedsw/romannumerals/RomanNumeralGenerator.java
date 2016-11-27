package org.craftedsw.romannumerals;

public class RomanNumeralGenerator {

	public static String romanFor(int decimal) {
		String roman = "";
		if (decimal == 5) {
			return "V";
		}
		for (int i = 0; i < decimal; i++) {
			roman  += "I";
		}
		
		return roman;
	}

}
