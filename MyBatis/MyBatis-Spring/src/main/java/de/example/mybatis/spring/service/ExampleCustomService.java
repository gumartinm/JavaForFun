package de.example.mybatis.spring.service;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import org.apache.log4j.Logger;

import de.example.mybatis.model.Ad;
import de.example.mybatis.repository.mapper.AdCustomMapper;


public class ExampleCustomService {
    private static final Logger logger = Logger.getLogger(ExampleService.class);

    private AdCustomMapper adCustomMapper;

    public void setAdCustomMapper(final AdCustomMapper adCustomMapper) {
        this.adCustomMapper = adCustomMapper;
    }
    
    public void getAds() {
    	logger.info("ExampleCustomService: getAds");
    	
    	final Set<Ad> ads = this.adCustomMapper.selectAds();
    	
        for (final Ad ad : ads) {
            logger.info("Ad id: " + ad.getId());
            if (ad.getAdGps() != null) {
                try {
                    logger.info("Ad GPS: " + new String(ad.getAdGps(), "UTF-8"));
                } catch (final UnsupportedEncodingException e) {
                    logger.error("Encoding error", e);
                }
            }
            logger.info("Ad mobileImage: " + ad.getAdMobileImage());
            logger.info("Ad companyCategId: " + ad.getCompanyCategId());
            logger.info("Ad companyId: " + ad.getCompanyId());
            logger.info("Ad createdAt: " + ad.getCreatedAt());
            logger.info("Ad updatedAt: " + ad.getUpdatedAt());
            logger.info("\n");
        }
    }
}
