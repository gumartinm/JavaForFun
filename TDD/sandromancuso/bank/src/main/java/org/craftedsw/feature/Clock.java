package org.craftedsw.feature;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Clock {

	public String todayAsString() {
		// We need to control this random stuff for our tests. :(
		LocalDate today = LocalDate.now();
		return today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

}
