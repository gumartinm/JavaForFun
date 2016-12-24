package de.spring.example.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Audited
@Table(name="AD_DESCRIPTION", schema="mybatis_example")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="jsonId")
public class AdDescription implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID", updatable=false, nullable=false)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JsonBackReference
	private Ad ad;
	
	@NotNull
	@Max(60)
	@Column(name="LANGUAGE_ID")
	private Long languageId;
	
	@NotNull
	@Size(min=2, max=255)
	@Column(name="AD_NAME")
	private String adName;

	@NotNull
	@Size(min=2, max=255)
	@Column(name="AD_DESCRIPTION")
	private String adDescription;
	
	@NotNull
	@Size(min=2, max=500)
	@Column(name="AD_MOBILE_TEXT")
	private String adMobileText;
	
	@NotNull
	@Size(min=2, max=3000)
	@Column(name="AD_LINK")
	private String adLink;
	
	// It will be used by JPA when filling the property fields with data coming from data base.
	protected AdDescription() {

	}

	// It will be used by my code (for example by Unit Tests)
	public AdDescription(Long id, Ad ad, Long languageId, String adName, String adDescription,
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

	public Long getId() {
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
