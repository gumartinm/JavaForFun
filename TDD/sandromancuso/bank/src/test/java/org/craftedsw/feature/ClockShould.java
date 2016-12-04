package org.craftedsw.feature;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ClockShould {

	@Test public void
	return_todays_date_in_dd_MM_yyyy_format() {
		Clock clock = new Clock();
		
		String date = clock.todayAsString();
		
		assertThat(date, is("04/12/2016"));
	}

}
