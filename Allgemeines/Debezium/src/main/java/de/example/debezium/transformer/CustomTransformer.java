package de.example.debezium.transformer;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Taken from https://github.com/debezium/debezium-examples/tree/master/outbox/event-routing-smt
 *
 */
public class CustomTransformer<R extends ConnectRecord<R>> implements Transformation<R> {
	private static final Logger logger = LoggerFactory.getLogger(CustomTransformer.class);

	private static final String TOPIC = "example.customtransformer";
	private static final String JSON_SCHEMA_URL = "https://gumartinm.name/events/customers/Customer.json/1.json";

	@Override
	public void configure(Map<String, ?> configs) {
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
			logger.info("Ignoring deletes");
			return null;
		}

		Long timestamp = struct.getInt64("ts_ms");
		OffsetDateTime published = OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC"));
		Struct after = struct.getStruct("after");
		Integer customerId = after.getInt32("id");
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

		return record.newRecord(TOPIC, null, null, customerId, null, customer, record.timestamp());
	}

	@Override
	public ConfigDef config() {
		return new ConfigDef();
	}

	@Override
	public void close() {
	}
}
