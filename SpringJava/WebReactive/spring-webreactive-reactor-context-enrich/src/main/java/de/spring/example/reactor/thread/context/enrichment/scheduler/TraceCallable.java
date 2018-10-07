package de.spring.example.reactor.thread.context.enrichment.scheduler;

import java.util.Map;
import java.util.concurrent.Callable;

import de.spring.example.reactor.thread.context.enrichment.ObjectContext;
import de.spring.example.reactor.thread.context.enrichment.ThreadContext;

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
