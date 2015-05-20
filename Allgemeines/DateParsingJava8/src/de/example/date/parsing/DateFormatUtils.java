package de.example.date.parsing;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateFormatUtils {

	public static String format(Date date, String format) {
		final ZoneId timeZone = ZoneId.of("America/Los_Angeles");
		
		return DateFormatUtils.format(date, format, timeZone);
	}

	public static String format(Date date, String format, ZoneId timeZone) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		final Instant instantDate = Instant.ofEpochMilli(date.getTime());
		final LocalDateTime localDateTime = LocalDateTime.ofInstant(instantDate, timeZone);
		
		return localDateTime.format(formatter);
	}

	public static Date parse(String text, String format) {
		final ZoneId timeZone = ZoneId.of("America/Los_Angeles");
		
		return DateFormatUtils.parse(text, format, timeZone);
	}

	public static Date parse(String text, String format, ZoneId timeZone) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		final LocalDateTime dateTimeJava8 = LocalDateTime.parse(text, formatter);
		
		return Date.from(dateTimeJava8.atZone(timeZone).toInstant());
	}
}
