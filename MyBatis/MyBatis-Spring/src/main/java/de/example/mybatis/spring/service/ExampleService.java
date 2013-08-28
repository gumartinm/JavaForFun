package de.example.mybatis.spring.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import de.example.mybatis.model.Ad;
import de.example.mybatis.model.AdCriteria;
import de.example.mybatis.repository.mapper.AdMapper;

public class ExampleService {
    private static final Logger logger = Logger.getLogger(ExampleService.class);

    private AdMapper adMapper;

    public void setAdMapper(final AdMapper adMapper) {
        this.adMapper = adMapper;
    }

    @Transactional /**There is not inserts so this is useless, anyhow this is just an example**/
    public void getAdsByCriteria() {
        logger.info("Using criteria");

        final AdCriteria adCriteria = new AdCriteria();

        adCriteria.or().andAdMobileImageEqualTo("mobileImage.jpg")
        .andCreatedAtNotEqualTo(new Date());

        adCriteria.or().andAdMobileImageNotEqualTo("noMobileImage.jpg")
        .andAdMobileImageIsNotNull();

        // where (ad_mobile_image = "mobileImage.jpg" and created_at <> Now())
        // or (ad_mobile_image <> "noMobileImage.jpg" and ad_mobile_image is not null)

        final List<Ad> adLists = this.adMapper.selectByExampleWithBLOBs(adCriteria);
        for (final Ad ad : adLists) {
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