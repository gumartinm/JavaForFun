package de.spring.example.persistence.domain.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/** import org.hibernate.envers.DefaultRevisionEntity; **/
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@RevisionEntity(MyCustomRevisionListener.class)
@Table(name="CUSTOM_REVISION", schema="mybatis_example")
public class MyCustomRevision /** extends DefaultRevisionEntity **/ {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID", updatable=false, nullable=false)
	@RevisionNumber
	// BE CAREFUL!!!! spring-data-envers JUST WORKS (I couldn't make it work with anything else) WITH Integer. NOT WITH Long :(
	private Integer id;
	
	@Column(name="REVISION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@RevisionTimestamp
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private Date revisionDate;
	
	@Column(name="USERNAME")
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
	
	public Integer getId() {
		return id;
	}
	
    public Date getRevisionDate() {
    	return revisionDate;
    }
	
    public String getUsername() {
    	return username;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
}
