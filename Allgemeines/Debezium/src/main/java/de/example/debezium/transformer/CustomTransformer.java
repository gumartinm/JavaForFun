package de.example.debezium.transformer;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Taken from https://github.com/debezium/debezium-examples/tree/master/outbox/event-routing-smt
 *
 */
public class CustomTransformer<R extends ConnectRecord<R>> implements Transformation<R> {
	private static final String TOPIC = "example.customtransformer";
	private static final String JSON_SCHEMA_URL = "https://gumartinm.name/events/customers/Customer.json/1.json";

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void configure(Map<String, ?> configs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public R apply(R record) {
		// Ignoring tombstones just in case
		if (record.value() == null) {
			return record;
		}

		Struct struct = (Struct) record.value();
		String op = struct.getString("op");

		// ignoring deletions in the events table
		if (op.equals("d")) {
			return null;
		} else if (op.equals("c") || op.equals("r")) {

			Long timestamp = struct.getInt64("ts_ms");
			OffsetDateTime published = OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC"));
			Struct after = struct.getStruct("after");
			Long customerId = after.getInt64("id");
			String firstName = after.getString("first_name");
			String lastName = after.getString("last_name");
			String email = after.getString("email");

			Base base = Base.builder()
					.withType("Customer")
					.withPublished(published)
			        .withSchema(JSON_SCHEMA_URL)
					.build();
			Customer customer = Customer.builder()
					.withBase(base)
					.withCustomerId(customerId)
					.withFirstName(firstName)
					.withLastName(lastName)
					.withEmail(email)
					.build();

			String json = convertToJson(customer);
			return record.newRecord(TOPIC, null, null, customerId, null, json, record.timestamp());
		} else {
			throw new IllegalArgumentException("Record of unexpected op type: " + record);
		}
	}

	@Override
	public ConfigDef config() {
		return new ConfigDef();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	private String convertToJson(Customer customer) {
		try {
			return objectMapper.writeValueAsString(customer);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}
}
