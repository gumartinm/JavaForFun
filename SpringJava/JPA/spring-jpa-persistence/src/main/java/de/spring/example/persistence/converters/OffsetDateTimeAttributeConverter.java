package de.spring.example.persistence.converters;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OffsetDateTimeAttributeConverter implements AttributeConverter<OffsetDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(OffsetDateTime offsetDateTime) {
		Timestamp timestamp = null;
		
		if (offsetDateTime != null) {
			timestamp = Timestamp.valueOf(offsetDateTime.toLocalDateTime());
		}
		
		return timestamp;
	}

	@Override
	public OffsetDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
		OffsetDateTime offsetDateTime = null;
		
		if (sqlTimestamp != null) {
			final LocalDateTime localDateTime = sqlTimestamp.toLocalDateTime();
			final ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
			offsetDateTime = zonedDateTime.toOffsetDateTime();
		}
		
		return offsetDateTime;
	}

}
