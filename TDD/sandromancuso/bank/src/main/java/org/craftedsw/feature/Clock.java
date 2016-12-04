package org.craftedsw.feature;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Clock {

	private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public String todayAsString() {
		return today().format(DD_MM_YYYY);
	}

	// We isolated the randomness in here.
	protected LocalDate today() {
		// QUESTION FOR SANDRO MANCUSO:
		// This line of code is not under test, so if someone makes
		// a change here our unit tests will not fail!!! :/
		// See possible answer in my ClockGus and ClockGusShould implementations.
		return LocalDate.now();
		// If someone writes:
		// return null
		// nothing happens, tests keep going green... :(
	}

}
