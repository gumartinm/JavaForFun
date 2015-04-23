package de.example.mybatis.spring.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;

import de.example.mybatis.model.Ad;
import de.example.mybatis.repository.mapper.ChildMapper;
import de.example.mybatis.repository.mapper.ParentMapper;


public class ExampleInheritanceService {
    private static final Logger logger = Logger.getLogger(ExampleService.class);

    private ChildMapper childMapper;
    private ParentMapper parentMapper;

    public void setChildMapper(final ChildMapper childMapper) {
        this.childMapper = childMapper;
    }
    
    public void setParentMapper(final ParentMapper parentMapper) {
        this.parentMapper = parentMapper;
    }
    
    public void selectAdsChild() {
    	logger.info("ExampleCustomService: getAds");
    	
    	final List<Ad> ads = this.childMapper.selectAdsChild();
    	
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
    
    public void selectAdsParent() {
    	logger.info("ExampleCustomService: updateAds");
    	
    	final List<Ad> ads = this.childMapper.selectAdsParent();

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
