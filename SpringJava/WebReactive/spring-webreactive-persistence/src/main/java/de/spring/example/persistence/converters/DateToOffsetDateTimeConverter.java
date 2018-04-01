package de.spring.example.persistence.converters;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

// IT DOES NOT WORK. I THINK THERE IS A BUG BECAUSE MappingMongoConverter.this.conversionService never includes my custom converters!!!!
// ALL THIS STUFF SUCKS A LOT!!!
public class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {

	@Override
	public OffsetDateTime convert(Date source) {
		return source == null ? null : OffsetDateTime.ofInstant(source.toInstant(), ZoneOffset.UTC);
	}

}
