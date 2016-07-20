package de.spring.example.persistence.domain.audit;

import org.hibernate.envers.RevisionListener;

import de.spring.example.context.UsernameThreadContext;

public class MyCustomRevisionListener implements RevisionListener {
	private final UsernameThreadContext userNameThreadContext;
	
	public MyCustomRevisionListener(UsernameThreadContext userNameThreadContext) {
		this.userNameThreadContext = userNameThreadContext;
	}
	
	@Override
	public void newRevision(Object revisionEntity) {
		MyCustomRevision myCustomRevision = (MyCustomRevision) revisionEntity;
		
		final String username = getSafeUsername();
		myCustomRevision.setUsername(username);
		
	}
	
	private String getSafeUsername() {
		String userName = userNameThreadContext.getUsername();
		
		if (userName == null) {
			userName = "NO_USER";
		}
		
		return userName;
	}

}
