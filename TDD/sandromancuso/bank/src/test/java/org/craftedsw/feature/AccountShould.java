package org.craftedsw.feature;

import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountShould {

	@Mock private TransactionRepository transactionRepository;
	private Account account;
	private StatementPrinter statementPrinter;

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
	
	@Test public void
	print_a_statement() {
		List<Transaction> transactions = null;
		
		verify(statementPrinter).print(transactions);
	}
 }
