package de.example.debezium.transformer;

import java.util.Map;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.connect.storage.Converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomConverter implements Converter {
	private final JsonSerializer serializer = new JsonSerializer();
	private final ObjectMapper objectMapper;

	public CustomConverter() {
		this.objectMapper = new ObjectMapper();
		// this.objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	}

	@Override
	public byte[] fromConnectData(String topic, Schema schema, Object value) {
		JsonNode jsonValue = convertToJson(value);
        try {
            return serializer.serialize(topic, jsonValue);
        } catch (SerializationException e) {
            throw new DataException("Converting Kafka Connect data to byte[] failed due to serialization error: ", e);
        }	
    }

	@Override
	public SchemaAndValue toConnectData(String topic, byte[] value) {
		return null;
	}

	private JsonNode convertToJson(Object value) {
		return objectMapper.convertValue(value, JsonNode.class);
	}
}
