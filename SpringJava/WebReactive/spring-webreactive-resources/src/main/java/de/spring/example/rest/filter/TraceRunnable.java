package de.spring.example.rest.filter;

import de.spring.example.context.UsernameContext;
import de.spring.example.context.UsernameThreadContext;

public class TraceRunnable implements Runnable {
    private final Runnable delegate;
	private final UsernameContext userNameParentContext;

    public TraceRunnable(Runnable delegate) {
		this.delegate = delegate;
		userNameParentContext = UsernameThreadContext.getUsernameContext();
	}

	@Override
	public void run() {
		UsernameThreadContext.setUsernameContext(userNameParentContext);
		try {
			this.delegate.run();
		} finally {
			UsernameThreadContext.clearUsernameContext();
		}
	}
}
