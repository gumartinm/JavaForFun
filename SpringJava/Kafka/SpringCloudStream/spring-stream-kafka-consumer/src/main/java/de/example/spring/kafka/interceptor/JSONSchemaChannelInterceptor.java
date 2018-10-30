package de.example.spring.kafka.interceptor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;

public class JSONSchemaChannelInterceptor implements ChannelInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(JSONSchemaChannelInterceptor.class);

	private final ObjectMapper objectMapper;

	private final JsonSchema jsonSchema;

	public JSONSchemaChannelInterceptor(ObjectMapper objectMapper, JsonSchema jsonSchema) {
		this.objectMapper = objectMapper;
		this.jsonSchema = jsonSchema;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		String jsonEventSerialized = getJsonEventSerialized(message);
		ProcessingReport processingMessages = validateEvent(jsonEventSerialized);
		if (!processingMessages.isSuccess()) {
			throw new RuntimeException("Validation error!!");
		}

		return message;
	}

	private String getJsonEventSerialized(Message<?> message) {
		Object payload = message.getPayload();
		if (payload instanceof byte[]) {
			return new String((byte[]) payload);
		} else {
			return payload.toString();
		}
	}

	private ProcessingReport validateEvent(String jsonEventSerialized) {
		try {
			return jsonSchema.validate(objectMapper.readTree(jsonEventSerialized));
		} catch (ProcessingException | IOException ex) {
			LOGGER.error("JSON schema, validation error.", ex);

			throw new RuntimeException("JSON schema, validation error.", ex);
		}
	}
}
