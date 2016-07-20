package de.spring.example.rest.handler;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.spring.example.context.UsernameThreadContext;

public class UsernameHandler extends HandlerInterceptorAdapter {
	private final UsernameThreadContext usernameThreadContext;
	
	@Inject
	public UsernameHandler(UsernameThreadContext userNameThreadContext) {
		this.usernameThreadContext = userNameThreadContext;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		final String userName = request.getHeader(UsernameThreadContext.USERNAME_HEADER);
		
		if (userName != null) {
			usernameThreadContext.setUsername(userName);
		} else {
			usernameThreadContext.clearUsername();
		}
		
		return super.preHandle(request, response, handler);
	}
}
