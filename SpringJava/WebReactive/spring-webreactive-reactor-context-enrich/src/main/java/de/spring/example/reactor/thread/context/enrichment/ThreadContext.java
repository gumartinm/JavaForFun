package de.spring.example.reactor.thread.context.enrichment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.MDC;

public class ThreadContext {
	private static final ThreadLocal<Map<String, ObjectContext>> contextHolder = new ThreadLocal<Map<String, ObjectContext>>() {
		
		@Override
		public Map<String, ObjectContext> initialValue() {
			return new HashMap<>();
		}
	};

	private ThreadContext() {}

	public static final void setContext(ObjectContext objectContext) {
		Objects.requireNonNull(objectContext, "ObjectContext, null value is not allowed");

		contextHolder.get().put(objectContext.getHeader(), objectContext);
		MDC.put(objectContext.getHeader(), objectContext.getValue());
	}

	public static final void setContexts(Map<String, ObjectContext> objectContexts) {
		Objects.requireNonNull(objectContexts, "Map<String, ObjectContext>, null value is not allowed");

		objectContexts.forEach((key, value) -> {
			MDC.put(value.getHeader(), value.getValue());
		});
		contextHolder.set(objectContexts);
	}

	public static final Map<String, ObjectContext> getContexts() {
		return contextHolder.get();
	}

	public static final void clearContexts() {
		contextHolder.get().forEach((key, value) -> {
			MDC.remove(key);
		});
		contextHolder.remove();
	}
}
