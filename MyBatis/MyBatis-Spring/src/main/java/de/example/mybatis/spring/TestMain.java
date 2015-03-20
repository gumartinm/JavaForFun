package de.example.mybatis.spring;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import de.example.mybatis.spring.service.BatchAndSimpleSameTrx;
import de.example.mybatis.spring.service.ExampleBatchService;
import de.example.mybatis.spring.service.ExampleCustomService;
import de.example.mybatis.spring.service.ExampleService;

public class TestMain {
    private static final Logger logger = Logger.getLogger(TestMain.class);

    public static void main(final String[] args) {

        logger.info("Starting application");

//        final ExampleService exampleService = (ExampleService) SpringContextLocator
//                .getInstance().getBean("exampleService");
//
//        exampleService.insertNewAd();
//
//        exampleService.getAdsByCriteria();
//
//
//        final ExampleCustomService exampleCustomService = (ExampleCustomService) SpringContextLocator
//                .getInstance().getBean("exampleCustomService");
//
//        exampleCustomService.getAds();
//        
//        exampleCustomService.updateAds();
        
        
//        final ExampleBatchService exampleBatchService = (ExampleBatchService) SpringContextLocator
//                .getInstance().getBean("exampleBatchService");
//        
//        exampleBatchService.insertNewAd();
//
//        exampleBatchService.insertBatchNewAd();
        
        
        final BatchAndSimpleSameTrx batchAndSimpleSameTrx = (BatchAndSimpleSameTrx) SpringContextLocator
                .getInstance().getBean("batchAndSimpleSameTrx");
        
        try {
	        batchAndSimpleSameTrx.insertNewAd();
        } catch (CannotGetJdbcConnectionException e) {
        	logger.error("Error exception: ", e);
        } catch (SQLException e) {
        	logger.error("Error exception: ", e);
        }
  
    }

}
