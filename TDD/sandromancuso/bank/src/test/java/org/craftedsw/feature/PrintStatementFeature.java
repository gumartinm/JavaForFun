package org.craftedsw.feature;

import static org.mockito.Mockito.inOrder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrintStatementFeature {

	@Mock private Console console;
	private Account account;
	
	@Before
	public void initialise() {
		// Acceptance test is using the real repository because the acceptance test
		// is testing the system as a whole. We are just mocking the external world (the Console)
		TransactionRepository transactionRepository = new TransactionRepository(); 
		account = new Account(transactionRepository);
	}
	
	@Test public void
	print_statement_containing_all_transactions() {
		account.deposit(1000);
		// No negative value, instead we use the verb withdraw.
		// Semantics are very important in the code!!!
		account.withdraw(100);
		account.deposit(500);
		
		account.printStatement();
		
		InOrder inOrder = inOrder(console);
		inOrder.verify(console).printLine("DATE | AMOUNT | BALANCE");
		inOrder.verify(console).printLine("10 / 04 / 2014 | 500.00 | 1400.00");
		inOrder.verify(console).printLine("02 / 04 / 2014 | -100.00 | 900.00");
		inOrder.verify(console).printLine("01 / 04 / 2014 | 1000.00 | 1000.00");
	}

}
