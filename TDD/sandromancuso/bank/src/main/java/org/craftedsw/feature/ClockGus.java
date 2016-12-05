package org.craftedsw.feature;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClockGus {

	private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	public String todayAsString() {
		return formatted(LocalDate.now());
	}

	protected String formatted(LocalDate date) {
		return date.format(DD_MM_YYYY);
	}
}
