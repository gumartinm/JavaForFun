package de.example.mybatis.spring.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.SQL;
import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;

import de.example.mybatis.model.Ad;
import de.example.mybatis.repository.mapper.AdMapper;


public class BatchAndSimpleSameTrx {
	private static final Logger logger = Logger.getLogger(BatchAndSimpleSameTrx.class);
	
    private AdMapper adMapper;
    private DataSource dataSource;
	
	@Transactional
	/** 
	 * JUST IN THIS VERY MOMENT Spring sends "set autocommit = 0" EVEN IF THERE ARE NO OPERATIONS ON THE DATABASE!!!
	 * So, Spring is picking up some thread from the connections pool and giving it to the current Java thread.
	 */
	public void insertNewAd() throws CannotGetJdbcConnectionException, SQLException {
		logger.info("Insert new Ad");

        final Ad adTest = new Ad();
        adTest.setAdMobileImage("bild.jpg");
        adTest.setCompanyCategId(200L);
        adTest.setCreatedAt(new Date());
        adTest.setCompanyId(2L);
        adTest.setUpdatedAt(new Date());
        
        /**
         * No batched inserts will be sent to data base just in this very moment.
         */
        this.adMapper.insert(adTest); 
        
        /**
         * We want to use SIMPLE and BATCH operations but MyBatis complains with this exception:
         * "Cannot change the ExecutorType when there is an existing transaction".
         * 
         * So, we get the connection for the current transaction by means of the Spring DataSourceUtils
         * and using JDBC we implement the BATCH operation in the current open transaction
         * (it was open because of the @Transactional annotation)
         */
        this.doBatch(DataSourceUtils.getConnection(this.dataSource));
        
        /**
         * No batched inserts will be sent to data base just in this very moment.
         */
        this.adMapper.insert(adTest);
        
        /**
         * WHEN RETURNING FROM THIS METHOD Spring SENDS "set autocommit = 1" AND TRANSACTION ENDS
         * (OR IF THERE IS ERROR Spring SENDS "rollback" AS EXPECTED)
         */
	}
	
	private void doBatch(final Connection connection) throws SQLException {

        final PreparedStatement preparedStatement = connection.prepareStatement(doStatement());
        try {
        	for (int i = 0; i < 10; i++) {
        		/**
        		 * Batched statements are not yet sent to data base.
        		 */
        		preparedStatement.addBatch();
        	}
        	
        	/**
        	 * RIGHT HERE THE BATCH STATEMENTS WILL BE SENT TO THE DATA BASE.
        	 */
        	preparedStatement.executeBatch();
        	
        } finally {
        	preparedStatement.close();
        }
	}
	
	private String doStatement() {
		return new SQL() {
			{
				INSERT_INTO("ad");
				VALUES("company_categ_id, ad_mobile_image, created_at, updated_at",
						"'200', 'batch.jpg', '2015-03-20 02:54:50.0', '2015-03-20 02:54:50.0'");
			}
		}.toString();
	}
    
    public void setAdMapper(final AdMapper adMapper) {
        this.adMapper = adMapper;
    }
    
    public void setDataSource(final DataSource dataSource) {
    	this.dataSource = dataSource;
    }
}
