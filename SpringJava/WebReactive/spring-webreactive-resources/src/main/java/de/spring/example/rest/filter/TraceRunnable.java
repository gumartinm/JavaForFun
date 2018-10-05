package de.spring.example.rest.filter;

import java.util.Map;

import de.spring.example.context.ObjectContext;
import de.spring.example.context.ThreadContext;

public class TraceRunnable implements Runnable {
    private final Runnable delegate;
	private final Map<String, ObjectContext> contexts;

    public TraceRunnable(Runnable delegate) {
		this.delegate = delegate;
		contexts = ThreadContext.getContexts();
	}

	@Override
	public void run() {
		ThreadContext.setContexts(contexts);
		try {
			this.delegate.run();
		} finally {
			ThreadContext.clearContexts();
		}
	}
}
