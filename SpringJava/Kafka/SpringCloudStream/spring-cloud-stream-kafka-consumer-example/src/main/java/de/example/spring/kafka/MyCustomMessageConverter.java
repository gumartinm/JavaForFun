package de.example.spring.kafka;

import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

public class MyCustomMessageConverter extends AbstractMessageConverter {

  public MyCustomMessageConverter() {
    super(new MimeType("application", "example"));
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return (String.class == clazz);
  }

  @Override
  protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
    Object payload = message.getPayload();

    logger.info("convertFromInternal, payload: " + payload);

	return (payload instanceof String ? payload : new String((byte[]) payload));
  }
}
