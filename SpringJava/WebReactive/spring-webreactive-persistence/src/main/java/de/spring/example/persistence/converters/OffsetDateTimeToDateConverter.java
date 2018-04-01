package de.spring.example.persistence.converters;

import java.time.OffsetDateTime;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;


//IT DOES NOT WORK. I THINK THERE IS A BUG BECAUSE MappingMongoConverter.this.conversionService never includes my custom converters!!!!
//ALL THIS STUFF SUCKS A LOT!!!
public class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {

	@Override
	public Date convert(OffsetDateTime source) {
		return source == null ? null : Date.from(source.toInstant());
	}

}
