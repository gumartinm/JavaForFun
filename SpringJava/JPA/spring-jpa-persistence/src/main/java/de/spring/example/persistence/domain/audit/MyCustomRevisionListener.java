package de.spring.example.persistence.domain.audit;

import org.hibernate.envers.RevisionListener;

import de.spring.example.context.UsernameThreadContext;

public class MyCustomRevisionListener implements RevisionListener {	
	
	// It will be used by Hibernate.
	protected MyCustomRevisionListener() {
		
	}
	
	@Override
	public void newRevision(Object revisionEntity) {
		MyCustomRevision myCustomRevision = (MyCustomRevision) revisionEntity;
		
		final String username = getSafeUsername();
		myCustomRevision.setUsername(username);
		
	}
	
	private String getSafeUsername() {
		String userName = UsernameThreadContext.getUsername();
		
		if (userName == null) {
			userName = "NO_USER";
		}
		
		return userName;
	}

}
