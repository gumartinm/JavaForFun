package de.spring.example.persistence.domain.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;

@Entity
@RevisionEntity(MyCustomRevisionListener.class)
@Table(name="CUSTOM_REVISION", schema="mybatis_example")
public class MyCustomRevision {
	private String username;
	
	// It will be used by JPA when filling the property fields with data coming from data base.
	protected MyCustomRevision() {
		
	}
	
	// It will be used by my code (for example by Unit Tests)
	public MyCustomRevision(String username) {
		this.username = username;
	}

	/**
	 * WARNING: JPA REQUIRES GETTERS!!!
	 */
	
    public String getUsername() {
    	return username;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
}
