package org.craftedsw.feature;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class ClockGusShould {
	private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	@Test public void
	return_todays_date_in_dd_MM_yyyy_format() {
		ClockGus clock = spy(new ClockGus());
		LocalDate today = today();
		given(clock.formatted(today)).willReturn(today.format(DD_MM_YYYY));
		
		String date = clock.todayAsString();
		
		assertThat(date, is(today.format(DD_MM_YYYY)));
		verify(clock, times(1)).formatted(today);
	}
	
	@Test public void
	return_date_in_dd_MM_yyyy_format() {
		ClockGus clock = new ClockGus();
		LocalDate today = today();
		
		String date = clock.formatted(today);
		
		assertThat(date, is(today.format(DD_MM_YYYY)));
	}

	private LocalDate today() {
		return LocalDate.now();
	}
}
