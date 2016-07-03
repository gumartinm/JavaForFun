package de.spring.persistence.example.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="ad_description", schema="mybatis_example")
public class AdDescription implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false, nullable=false)
	private Long id;
	
	@NotNull
	@ManyToOne(optional=false)
	@JoinColumn(name="ad_id", referencedColumnName="id")
	private Ad ad;
	
	@NotNull
	@Max(60)
	@Column(name="language_id")
	private Long languageId;
	
	@NotNull
	@Size(min=2, max=255)
	@Column(name="ad_name")
	private String adName;

	@NotNull
	@Size(min=2, max=255)
	@Column(name="ad_description")
	private String adDescription;
	
	@NotNull
	@Size(min=2, max=500)
	@Column(name="ad_mobile_text")
	private String adMobileText;
	
	@NotNull
	@Size(min=2, max=3000)
	@Column(name="ad_link")
	private String adLink;
	
	// It will be used by JPA when filling the property fields with data coming from data base.
	protected AdDescription() {

	}

	// It will be used by my code (for example by Unit Tests)
	public AdDescription(Long id, Ad ad, Long languageId, String adName, String adDescription,
			String adMobileText, String adLink) {
		this.id = id;
		this.languageId = languageId;
		this.ad = ad;
		this.adName = adName;
		this.adDescription = adDescription;
		this.adMobileText = adMobileText;
		this.adLink = adLink;
	}

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