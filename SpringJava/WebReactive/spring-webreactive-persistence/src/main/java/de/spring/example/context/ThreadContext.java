package de.spring.example.context;

import org.slf4j.MDC;

public abstract class ThreadContext {
	private static final ThreadLocal<ThreadContext> contextHolder = new ThreadLocal<>();

	abstract String getValue();

	abstract String getHeader();

	public static final void setContextValue(ThreadContext contextValue) {

		if (contextValue == null) {
			contextHolder.remove();
			MDC.remove(contextValue.getHeader());
		} else {
			contextHolder.set(contextValue);
			MDC.put(contextValue.getHeader(), contextValue.getValue());
		}

		contextHolder.set(contextValue);
	}

	public static final void clearContextValue() {
		contextHolder.remove();
		MDC.remove(contextValue.getHeader());
	}

	public static final ThreadContext getContextValue() {
		return contextHolder.get();
	}
}
