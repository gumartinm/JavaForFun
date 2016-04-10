package de.example.mybatis.spring.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.example.mybatis.model.Ad;
import de.example.mybatis.repository.mapper.AdMapper;
import de.example.mybatis.spring.service.ExampleService;

@Service("exampleService")
public class ExampleServiceImpl implements ExampleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleServiceImpl.class);

    private final AdMapper adMapper;

    @Autowired
    public ExampleServiceImpl(AdMapper adMapper) {
    	this.adMapper = adMapper;
    }

    @Override
    @Transactional
	public void listAds() {
		LOGGER.info("listAds");

		final Set<Ad> ads = adMapper.selectAsSet();
		for (final Ad ad : ads) {
			LOGGER.info("Ad id: " + ad.getId());
			if (ad.getAdGps() != null) {
				try {
					LOGGER.info("Ad GPS: " + new String(ad.getAdGps(), "UTF-8"));
				} catch (final UnsupportedEncodingException e) {
					LOGGER.error("Encoding error", e);
				}
			}
			LOGGER.info("Ad mobileImage: " + ad.getAdMobileImage());
			LOGGER.info("Ad companyCategId: " + ad.getCompanyCategId());
			LOGGER.info("Ad companyId: " + ad.getCompanyId());
			LOGGER.info("Ad createdAt: " + ad.getCreatedAt());
			LOGGER.info("Ad updatedAt: " + ad.getUpdatedAt());
			LOGGER.info("\n");
		}
	}

    @Override
	@Transactional
	public void insertAndUpdateAds() {
		LOGGER.info("Insert two new Ads");

		final Ad adTestOne = new Ad();
		adTestOne.setAdMobileImage("bildOne.jpg");
		adTestOne.setCompanyCategId(200L);
		adTestOne.setCreatedAt(new Date());
		adTestOne.setCompanyId(2L);
		adTestOne.setUpdatedAt(new Date());
		adMapper.insert(adTestOne);

		final Ad adTestTwo = new Ad();
		adTestTwo.setAdMobileImage("bildTwo.jpg");
		adTestTwo.setCompanyCategId(200L);
		adTestTwo.setCreatedAt(new Date());
		adTestTwo.setCompanyId(3L);
		adTestTwo.setUpdatedAt(new Date());
		adMapper.insert(adTestTwo);

		
		
		LOGGER.info("Update two Ads");

		adTestOne.setAdMobileImage("updatedBildOne.jpg");
		adMapper.updateByPrimaryKey(adTestOne);
		
		adTestTwo.setAdMobileImage("updatedBildTwo.jpg");
		adMapper.updateByPrimaryKey(adTestTwo);
		
		
		
		
		LOGGER.info("Insert two new Ads");
		
		final Ad adTestThree = new Ad();
		adTestThree.setAdMobileImage("bildThree.jpg");
		adTestThree.setCompanyCategId(200L);
		adTestThree.setCreatedAt(new Date());
		adTestThree.setCompanyId(2L);
		adTestThree.setUpdatedAt(new Date());
		adMapper.insert(adTestThree);
		
		
		final Ad adTestFour = new Ad();
		adTestFour.setAdMobileImage("bildFour.jpg");
		adTestFour.setCompanyCategId(200L);
		adTestFour.setCreatedAt(new Date());
		adTestFour.setCompanyId(2L);
		adTestFour.setUpdatedAt(new Date());
		adMapper.insert(adTestFour);
		
		
		listAds();
	}
}