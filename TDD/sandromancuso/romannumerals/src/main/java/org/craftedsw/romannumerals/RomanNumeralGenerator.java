package org.craftedsw.romannumerals;

public class RomanNumeralGenerator {

	public static String romanFor(int decimal) {
		String roman = "";
		for (RomanToDecimal romanToDecimal : RomanToDecimal.values()) {
			if (decimal >= romanToDecimal.decimal) {
				roman += romanToDecimal.roman;
				decimal -= romanToDecimal.decimal;
			}
		}
		for (int i = 0; i < decimal; i++) {
			roman  += "I";
		}
		
		return roman;
	}

	enum RomanToDecimal {
		TEN("X", 10),
		FIVE("V", 5);
		
		private final String roman;
		private final int decimal;
		
		private RomanToDecimal(String roman, int decimal) {
			this.roman = roman;
			this.decimal = decimal;
		}
	}
}
