package de.example.date.parsing;

import java.util.Date;


public class DateHourFormatter {

	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	private DateHourFormatter() {
	}

	public static String formatToDateTime(Date date) {
		if (date == null) {
			return "";
		}
		return DateFormatUtils.format(date, DATE_FORMAT);
	}

	public static Date parseToDate(String text) {
		return DateFormatUtils.parse(text, DATE_FORMAT);
	}

}
