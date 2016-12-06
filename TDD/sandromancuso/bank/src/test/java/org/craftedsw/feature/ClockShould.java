package org.craftedsw.feature;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class ClockShould {

//  Sandro Mancuso original solution:
//	@Test public void
//	return_todays_date_in_dd_MM_yyyy_format() {
//		Clock clock = new TestableClock();
//
//		String date = clock.todayAsString();
//
//		assertThat(date, is("04/12/2016"));
//	}
//
//	private class TestableClock extends Clock {
//
//		@Override
//		protected LocalDate today() {
//			// We will control the random part of our code
//			// (no need of using spy)
//			return LocalDate.of(2016, 12, 4);
//		}
//	}

// 	Ivan Lorenz solution for the coverage problem:
	private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// We will control the random part of our code
    private LocalDate today;


    @Test public void
    return_todays_date_in_dd_MM_yyyy_format () {
        Clock clock = new TestableClock();

        String date = clock.todayAsString();

        assertThat(date, is(today.format(DD_MM_YYYY)));
    }

    private class TestableClock extends Clock {

        @Override
        protected LocalDate today() {
        	// We will control the random part of our code
            today = super.today();
            return today;
        }
    }
}
