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
		// BATCH MODE: no transaction
		// adTestOne.id = VALUE, AUTOINCREMENT FROM DATABASE
		// BATCH MODE: transaction
		// adTestOne.id = null  <-------------- IF YOU WANT TO USE BATCH MODE, YOUR CODE MUST BE IMPLEMENTED KNOWING THIS KIND OF STUFF!!!!
		// SIMPLE MODE: no transaction
		// adTestOne.id = VALUE, AUTOINCREMENT FROM DATABASE
		// SIMPLE MODE: transaction
		// adTestOne.id = VALUE, AUTOINCREMENT FROM DATABASE
		// REUSE MODE: no transaction
		// adTestOne.id = VALUE, AUTOINCREMENT FROM DATABASE
		// REUSE MODE: transaction
		// adTestOne.id = VALUE, AUTOINCREMENT FROM DATABASE
		
		// What is good about REUSE? When running this code in some transaction (using @Transactional)
		// the PreparedStatement being used for INSERT can be reused in the next INSERT (some lines below)
		// As always there is a cache ONLY for DMLs where the key is the statement. If the statement is the same
		// the PreparedStatement can be reused. Reusing some PreparedStatement means there is no need to call again
		// getConnection saving in this way CPU cycles. Besides when running in some transaction getConnection
		// will always return the same connection so, again calling getConnection is not needed.
		// If there is no transaction the cache is useless because after every DML will be cleaned up. Without transaction
		// REUSE and SIMPLE are the same. With transaction REUSE skips the code calling getConnection. With transaction
		// getConnection returns always the same connection. So, with transaction we can avoid some CPU cycles using REUSE.
		
		// With Spring-MyBatis operations are ONLY batched when running this code in some transaction. Otherwise BATCH mode is useless
		// because statements will be flushed once they are executed.

		final Ad adTestTwo = new Ad();
		adTestTwo.setAdMobileImage("bildTwo.jpg");
		adTestTwo.setCompanyCategId(200L);
		adTestTwo.setCreatedAt(new Date());
		adTestTwo.setCompanyId(3L);
		adTestTwo.setUpdatedAt(new Date());
		adMapper.insert(adTestTwo);

		
		
		LOGGER.info("Update two Ads");

		adTestOne.setAdMobileImage("updatedBildOne.jpg");
		// WARNING!!! adTestOne.id keeps being NULL when using BATCH Executor of MyBatis!!!!
		//            So, this code will do ANYTHING AT ALL!!!!
		// BE CAREFUL WHEN USING BATCH MODE FOR ACCESSING DATA BASES!!!!
		long countOne = adMapper.updateByPrimaryKey(adTestOne);
		
		// WARNING!!! adTestTwo.id keeps being NULL when using BATCH Executor of MyBatis!!!!
		//            So, this code will do ANYTHING AT ALL!!!!
		// BE CAREFUL WHEN USING BATCH MODE FOR ACCESSING DATA BASES!!!!
		adTestTwo.setAdMobileImage("updatedBildTwo.jpg");
		long countTwo = adMapper.updateByPrimaryKey(adTestTwo);
		
		// IF YOU WANT BATCH MODE FOR ACCESSING DATA BASES YOUR CODE MUST BE IMPLEMENTED FOR BATCH MODE.
		// I MEAN, IN THIS EXAMPLE SIMPLE MODE WILL WORK BUT BATCH MODE WILL NOT WORK IN ANY WAY!!!!
		// BATCH has some implications that must not be forgotten. You can not abstract your code
		// from the access mode to your data base!!!!!
		
		// BATCH MODE: no transaction
		// countOne.id = BatchExecutor.BATCH_UPDATE_RETURN_VALUE  <-------------- IF YOU WANT TO USE BATCH MODE, YOUR CODE MUST BE IMPLEMENTED KNOWING THIS KIND OF STUFF!!!!
		// But because there is no transaction statement is always flushed once it is executed.
		// So, in BATCH mode and without transaction we do not return the number of updated rows but the statement was
		// immediately flushed. :/
		// BATCH MODE: transaction
		// countOne.id = BatchExecutor.BATCH_UPDATE_RETURN_VALUE  <-------------- IF YOU WANT TO USE BATCH MODE, YOUR CODE MUST BE IMPLEMENTED KNOWING THIS KIND OF STUFF!!!!
		// SIMPLE MODE: no transaction
		// countOne.id = 1
		// SIMPLE MODE: transaction
		// countOne.id = 1
		// REUSE MODE: no transaction
		// countOne.id = 1
		// REUSE MODE: transaction
		// countOne.id = 1
		
		// With Spring-MyBatis operations are ONLY batched when running this code in some transaction. Otherwise BATCH mode is useless.
		// because statements will be flushed once they are executed.
		
		
		
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