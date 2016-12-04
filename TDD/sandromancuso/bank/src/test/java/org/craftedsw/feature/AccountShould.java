package org.craftedsw.feature;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountShould {

	@Mock private TransactionRepository transactionRepository;
	private Account account;

	@Before
	public void initialise() {
		account = new Account(transactionRepository);
	}
	
	@Test public void
	store_a_deposit_transaction() {
		account.deposit(100);
	
		verify(transactionRepository).addDeposit(100);
	}

	@Test public void
	store_a_withdrawal_transaction() {
		account.withdraw(100);
		
		verify(transactionRepository).addWithdrawal(100);
	}
}
