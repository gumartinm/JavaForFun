package de.spring.example.persistence.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.OffsetDateTime;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import de.spring.example.persistence.domain.Ad;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath*:spring-configuration/*.xml",
	"classpath*:spring-configuration-test/*.xml"} )
@Transactional
public class AdRespositoryShould {
	// Ad
	public static final Long COMPANY_ID = 2L;
	public static final Long COMPANY_CATEG_ID = 3L;
	public static final String AD_MOBILE_IMAGE = "slippers.jpg";
	public static final OffsetDateTime CREATED_AT = OffsetDateTime.now().minusDays(1);
	public static final OffsetDateTime UPDATED_AT = OffsetDateTime.now();

	@Inject
	AdRepository adRepository;
	
	@Test public void
	find_one_ad_by_id() {
		Ad ad = createAd();
		Ad createdAd = adRepository.save(ad);
		
		Ad storedAd = adRepository.findOne(createdAd.getId());
		
		assertThat(createdAd, is(storedAd));
	}
	
	@Test public void
	find_one_ad_by_id_using_native_query() {
		Ad ad = createAd();
		Ad createdAd = adRepository.save(ad);
		
		Ad storedAd = adRepository.findByIdNativeQuery(createdAd.getId());
				
		assertThat(createdAd, is(storedAd));
	}
	
	@Test public void
	find_one_ad_by_id_using_named_query() {
		Ad ad = createAd();
		Ad createdAd = adRepository.save(ad);
		
		Ad storedAd = adRepository.findByIdQuery(createdAd.getId());
				
		assertThat(createdAd, is(storedAd));
	}
	
	private static final Ad createAd() {	
		return new Ad(null, null, COMPANY_ID, COMPANY_CATEG_ID, AD_MOBILE_IMAGE,
				CREATED_AT, UPDATED_AT);
	}
}
