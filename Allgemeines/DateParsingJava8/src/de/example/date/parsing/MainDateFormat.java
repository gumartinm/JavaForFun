package de.example.date.parsing;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class MainDateFormat {

	public static void main(String[] args) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		ZoneId timeZone = ZoneId.of("America/Los_Angeles");
		
		// Just date without minutes and seconds.
		LocalDate dateJava8 = LocalDate.parse("12/05/2014 18:40", formatter);
		Date date = Date.from(dateJava8.atStartOfDay().atZone(timeZone).toInstant());
		
		// Date with minutes and seconds.
		LocalDateTime dateTimeJava8  = LocalDateTime.parse("12/05/2014 18:40", formatter);
		Date dateTime = Date.from(dateTimeJava8.atZone(timeZone).toInstant());
		
		Date now = new Date();
		Instant instant = Instant.ofEpochMilli(now.getTime());
		LocalDateTime currentLocalDateTime = LocalDateTime.ofInstant(instant, timeZone);
		String currentTime = currentLocalDateTime.format(formatter);

		String dateHourCurrentTimeFormatter = DateHourFormatter.formatToDateTime(now);
		
		
		Date dateCurrentTimeFormatter = DateHourFormatter.parseToDate("12/05/2014 18:40");
	}

}
