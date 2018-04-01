package de.spring.example.persistence.domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class, property="jsonId")
@Document
public class AdDescription implements Serializable {
	// https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo-template.id-handling
	// So, no way of using Long :(
	@Id
	private String id;
	
	@JsonBackReference
	private Ad ad;
	
	@NotNull
	@Max(60)
	private Long languageId;
	
	@NotNull
	@Size(min=2, max=255)
	private String adName;

	@NotNull
	@Size(min=2, max=255)
	private String adDescription;
	
	@NotNull
	@Size(min=2, max=500)
	private String adMobileText;
	
	@NotNull
	@Size(min=2, max=3000)
	private String adLink;
	
	// It will be used by JPA when filling the property fields with data coming from data base.
	protected AdDescription() {

	}

	// It will be used by my code (for example by Unit Tests)
	public AdDescription(String id, Ad ad, Long languageId, String adName, String adDescription,
			String adMobileText, String adLink) {
		this.id = id;
		this.ad = ad;
		this.languageId = languageId;
		this.adName = adName;
		this.adDescription = adDescription;
		this.adMobileText = adMobileText;
		this.adLink = adLink;
	}
	
	/**
	 * WARNING: JPA REQUIRES GETTERS!!!
	 */

	public String getId() {
		return id;
	}

	public Ad getAd() {
		return ad;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public String getAdName() {
		return adName;
	}

	public String getAdDescription() {
		return adDescription;
	}

	public String getAdMobileText() {
		return adMobileText;
	}

	public String getAdLink() {
		return adLink;
	}
}
