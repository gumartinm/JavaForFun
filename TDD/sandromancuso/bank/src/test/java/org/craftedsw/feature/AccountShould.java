package org.craftedsw.feature;

import static org.mockito.Mockito.verify;

import org.junit.Test;

public class AccountShould {

	private TransactionRepository transactionRepository;

	@Test public void
	store_a_deposit_transaction() {
		
	
		verify(transactionRepository).addDeposit(100);
	}

}
