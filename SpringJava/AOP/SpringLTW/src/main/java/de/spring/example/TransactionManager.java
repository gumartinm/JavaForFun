package de.spring.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class TransactionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);
    private static TransactionManager instance = new TransactionManager();
    private DataSourceTransactionManager transactionManager;
    private TransactionStatus transactionStatus;

    //Why could you want to extend this class?
    private TransactionManager() {
    }

    public static TransactionManager getInstance() {
        return instance;
    }

    public void initTransaction()
    {
    	LOGGER.info("initTRANSACTION");
        // transactionStatus = this.transactionManager.getTransaction(null);
    }

    public void rollbackTransaction()
    {
        this.transactionManager.rollback(this.transactionStatus);
    }


    public void commitTransaction()
    {
    	LOGGER.info("commitTRANSACTION");
        // this.transactionManager.commit(this.transactionStatus);
    }


    /************************* Setters and getters *******************************************/
    public void setTransactionManager(final DataSourceTransactionManager  transactionManager)
    {
        this.transactionManager = transactionManager;
    }
}
