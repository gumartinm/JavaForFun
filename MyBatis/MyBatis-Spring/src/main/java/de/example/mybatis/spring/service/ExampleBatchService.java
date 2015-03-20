package de.example.mybatis.spring.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import de.example.mybatis.batch.repository.mapper.AdSpringBatchMapper;
import de.example.mybatis.model.Ad;
import de.example.mybatis.repository.mapper.AdMapper;


public class ExampleBatchService {
	private static final Logger logger = Logger.getLogger(ExampleBatchService.class);
	
	private AdSpringBatchMapper adSpringBatchMapper;
    private AdMapper adMapper;

	@Transactional 
	/** 
	 * JUST IN THIS VERY MOMENT Spring sends "set autocommit = 0" EVEN IF THERE ARE NO OPERATIONS ON THE DATABASE!!!
	 * So, Spring is picking up some thread from the connections pool and giving it to the current Java thread.
	 */
    public void insertBatchNewAd() {
        logger.info("Insert new Batch Ad");

        final Ad adTest = new Ad();
        adTest.setAdMobileImage("bild.jpg");
        adTest.setCompanyCategId(200L);
        adTest.setCreatedAt(new Date());
        adTest.setCompanyId(2L);
        adTest.setUpdatedAt(new Date());
        
        /**
         * BECAUSE THIS IS A BATCH MAPPER, OPERATIONS WILL NOT BE SENT TO THE DATABASE UNTIL COMMAND commit IS SENT TO DATABASE.
         * BECAUSE THIS METHOD IS ANNOTATED WITH @Transactional, THE COMMAND commit WILL BE SENT WHEN RETURNING FROM THIS METHOD
         * (WHEN TRANSACTION ENDS UP)
         * 
         * WORK AROUND: using SqlSession (MyBatis) we have access to the flushStatements method!!!
         * SO, WE MUST INJECT THE SqlSession BEAN AND USE IT HERE IF WE DON'T WANT TO WAIT UNTIL TRANSACTION IS FINISHED.
         */
        this.adSpringBatchMapper.insert(adTest);
        
        /**
         * There will not be new "set autocommit = 0" because Spring remembers that this thread already started some transaction
         * (it must be using ThreadLocals)
         */
        this.insertPrivateNewAd();
    }
	
	private void insertPrivateNewAd() {
		logger.info("Insert new private Ad");

        final Ad adTest = new Ad();
        adTest.setAdMobileImage("bild.jpg");
        adTest.setCompanyCategId(200L);
        adTest.setCreatedAt(new Date());
        adTest.setCompanyId(2L);
        adTest.setUpdatedAt(new Date());
        
        // MyBatis complains: "Cannot change the ExecutorType when there is an existing transaction".
        // Work around in BatchAndSimpleSameTrx.
        this.adMapper.insert(adTest); 
	}
	
	@Transactional
	/** 
	 * JUST IN THIS VERY MOMENT Spring sends "set autocommit = 0" EVEN IF THERE ARE NO OPERATIONS ON THE DATABASE!!!
	 * So, Spring is picking up some thread from the connections pool and giving it to the current Java thread.
	 */
	public void insertNewAd() {
		logger.info("Insert new Ad");

        final Ad adTest = new Ad();
        adTest.setAdMobileImage("bild.jpg");
        adTest.setCompanyCategId(200L);
        adTest.setCreatedAt(new Date());
        adTest.setCompanyId(2L);
        adTest.setUpdatedAt(new Date());
        this.adMapper.insert(adTest); 
        
        //DataSourceUtils.getConnection();
        
        this.insertPrivateBatchNewAd();
	}
	
    private void insertPrivateBatchNewAd() {
        logger.info("Insert new private Batch Ad");

        final Ad adTest = new Ad();
        adTest.setAdMobileImage("bild.jpg");
        adTest.setCompanyCategId(200L);
        adTest.setCreatedAt(new Date());
        adTest.setCompanyId(2L);
        adTest.setUpdatedAt(new Date());
        
        /**
         * BECAUSE THIS IS A BATCH MAPPER, OPERATIONS WILL NOT BE SENT TO THE DATABASE UNTIL COMMAND commit IS SENT TO DATABASE.
         * BECAUSE THIS METHOD IS ANNOTATED WITH @Transactional, THE COMMAND commit WILL BE SENT WHEN RETURNING FROM THIS METHOD
         * (WHEN TRANSACTION ENDS UP)
         * 
         * WORK AROUND: using SqlSession (MyBatis) we have access to the flushStatements method!!!
         * SO, WE MUST INJECT THE SqlSession BEAN AND USE IT HERE IF WE DON'T WANT TO WAIT UNTIL TRANSACTION IS FINISHED.
         */
        // MyBatis complains: "Cannot change the ExecutorType when there is an existing transaction".
        // Workaround in BatchAndSimpleSameTrx.
        this.adSpringBatchMapper.insert(adTest);
    }
	
    public void setAdSpringBatchMapper(final AdSpringBatchMapper adSpringBatchMapper) {
    	this.adSpringBatchMapper = adSpringBatchMapper;
    }
    
    public void setAdMapper(final AdMapper adMapper) {
        this.adMapper = adMapper;
    }
}
