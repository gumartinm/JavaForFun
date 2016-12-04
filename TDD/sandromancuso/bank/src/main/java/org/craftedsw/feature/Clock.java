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
		return LocalDate.now();
	}

}
