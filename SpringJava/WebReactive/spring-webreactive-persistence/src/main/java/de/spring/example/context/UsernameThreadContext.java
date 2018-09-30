package de.spring.example.context;

import org.slf4j.MDC;

/**
 * I had to implement this class in a static way because JPA Entity objects do not allow you
 * to inject beans. StaticContextHolder did not either work :(
 * No way of injecting beans in JPA Entity classes :( 
 */
public class UsernameThreadContext {
	public static final String USERNAME_HEADER = "USERNAME";
	
	private static final ThreadLocal<UsernameContext> contextHolder = new ThreadLocal<>();
	
	public static final void setUsernameContext(UsernameContext usernameContext) {
		if (usernameContext == null) {
			contextHolder.remove();
			MDC.remove(USERNAME_HEADER);
		} else {
			contextHolder.set(usernameContext);
			MDC.put(USERNAME_HEADER, usernameContext.getUsername());
		}
	}
	
	public static final UsernameContext getUsernameContext() {
		return contextHolder.get();
	}
	
	public static final void clearUsernameContext() {
		contextHolder.remove();
		MDC.remove(USERNAME_HEADER);
	}
}
