package de.spring.example.persitence.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.persistence.domain.AdDescription;

public class AdTest {
	// Ad
	public static final String AD_ID = "66";
	public static final Long COMPANY_ID = 2L;
	public static final Long COMPANY_CATEG_ID = 3L;
	public static final String AD_MOBILE_IMAGE = "slippers.jpg";
	public static final Date CREATED_AT = Date.valueOf(LocalDate.now().minusDays(1));
	public static final Date UPDATED_AT = Date.valueOf(LocalDate.now());

	// AdDescription
	public static final String AD_DESCRIPTION_ID = "99";
	public static final Long LANGUAGE_ID = 3L;
	public static final String AD_NAME = "Slippers";
	public static final String AD_DESCRIPTION = "Slippers";
	public static final String AD_MOBILE_TEXT = "Buy it now!";
	public static final String AD_LINK = "http://gumartinm.name";
	public static final Set<AdDescription> AD_DESCRIPTIONS = createAdDescriptions();
	

	@Test
	public void whenCallingConstructorWithParametersThenCreateObject() {
		Ad ad = createAd();
		
		assertThat(ad.getAdDescriptions(), is(AD_DESCRIPTIONS));
		assertThat(ad.getAdMobileImage(), is(AD_MOBILE_IMAGE));
		assertThat(ad.getCompanyCategId(), is(COMPANY_CATEG_ID));
		assertThat(ad.getCompanyId(), is(COMPANY_ID));
		assertThat(ad.getCreatedAt(), is(CREATED_AT));
		assertThat(ad.getUpdatedAt(), is(UPDATED_AT));
		assertThat(ad.getId(), is(AD_ID));
	}
	
	private static final Ad createAd() {	
		return new Ad(AD_ID, AD_DESCRIPTIONS, COMPANY_ID, COMPANY_CATEG_ID, AD_MOBILE_IMAGE,
				CREATED_AT, UPDATED_AT);
	}
	
	private static final AdDescription createAdDescription() {
		return new AdDescription(AD_DESCRIPTION_ID, null, LANGUAGE_ID, AD_NAME, AD_DESCRIPTION,
				AD_MOBILE_TEXT, AD_LINK);
	}
	
	private static final Set<AdDescription> createAdDescriptions() {
		AdDescription adDescription = createAdDescription();
		Set<AdDescription> adDescriptions = new HashSet<>();
		adDescriptions.add(adDescription);
		
		return adDescriptions;
	}
}
