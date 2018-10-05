package de.spring.example.rest.filter;

import java.util.Map;
import java.util.concurrent.Callable;

import de.spring.example.context.ObjectContext;
import de.spring.example.context.ThreadContext;

public class TraceCallable<V> implements Callable<V> {
	private final Callable<V> delegate;
	private final Map<String, ObjectContext> contexts;

    public TraceCallable(Callable<V> delegate) {
		this.delegate = delegate;
		contexts = ThreadContext.getContexts();
	}

	@Override public V call() throws Exception {
		ThreadContext.setContexts(contexts);
		try {
			return this.delegate.call();
		} finally {
			ThreadContext.clearContexts();
		}
	}
}
