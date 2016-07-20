package de.spring.example.persistence.domain.audit;

import javax.inject.Inject;

import org.hibernate.envers.RevisionListener;

import de.spring.example.context.UsernameThreadContext;

public class MyCustomRevisionListener implements RevisionListener {
	@Inject
	private UsernameThreadContext userNameThreadContext;
	
	
	protected MyCustomRevisionListener() {
		
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
