package de.spring.example.persistence.converters;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
		Timestamp timestamp = null;
		
		if (localDateTime != null) {
			timestamp = Timestamp.valueOf(localDateTime);
		}
		
		return timestamp;
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
		LocalDateTime localDateTime = null;
		
		if (sqlTimestamp != null) {
			localDateTime = sqlTimestamp.toLocalDateTime();
		}
		
		return localDateTime;
	}

}
