package de.spring.example;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;


public class TransactionManagerN2A {
	private static TransactionManagerN2A instance = new TransactionManagerN2A();
	private DataSourceTransactionManager transactionManager;
	private TransactionStatus transactionStatus;

	//Why could you want to extend this class?
	private TransactionManagerN2A() {}
	
	public static TransactionManagerN2A getInstance() {
        return instance;
	}
	
	public void initTransaction()
	{
		transactionStatus = this.transactionManager.getTransaction(null);
	}
	
	public void rollbackTransaction()
	{
		this.transactionManager.rollback(this.transactionStatus);
	}
	
	public void commitTransaction()
	{
		this.transactionManager.commit(this.transactionStatus);
	}
	
	
	/************************* Setters and getters *******************************************/
	public void setTransactionManager(final DataSourceTransactionManager  transactionManager) 
	{
		this.transactionManager = transactionManager;
	}
}

