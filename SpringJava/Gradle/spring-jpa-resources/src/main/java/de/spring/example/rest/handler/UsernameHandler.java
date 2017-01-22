package de.spring.example.rest.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.spring.example.context.UsernameThreadContext;

public class UsernameHandler extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		final String userName = request.getHeader(UsernameThreadContext.USERNAME_HEADER);
		
		if (userName != null) {
			UsernameThreadContext.setUsername(userName);
		} else {
			UsernameThreadContext.clearUsername();
		}
		
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		UsernameThreadContext.clearUsername();
	}
}
