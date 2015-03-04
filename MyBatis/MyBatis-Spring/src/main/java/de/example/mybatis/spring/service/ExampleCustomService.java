package de.example.mybatis.spring.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
    
    public void updateAds() {
    	logger.info("ExampleCustomService: updateAds");
    	
    	final List<Ad> ads = this.adCustomMapper.selectAdsList();
    	final List<Ad> updateAds = new ArrayList<Ad>();
    	
    	long companyId = 10;
    	long companyCategId = 20;
    	String mobileImage = "newimage.jpg";
        for (final Ad ad : ads) {
        	ad.setCompanyCategId(companyCategId);
        	ad.setCompanyId(companyId);
        	ad.setAdMobileImage(mobileImage);
        	
        	updateAds.add(ad);
        	companyId++;
        	companyCategId++;
        }
        
        this.adCustomMapper.updateAdsBatch(updateAds);
    }
}
