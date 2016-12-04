package org.craftedsw.feature;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StatementPrinterShould {

	private static final List<Transaction> NO_TRANSACTIONS = Collections.emptyList();
	@Mock private Console console;

	@Test public void
	always_print_the_header() {
		StatementPrinter statementPrinter = new StatementPrinter(console);
		
		statementPrinter.print(NO_TRANSACTIONS);
		
		verify(console).printLine("DATE | AMOUNT | BALANCE");
	}

	@Test public void
	print_transactions_in_reverse_chronological_order() {
		StatementPrinter statementPrinter = new StatementPrinter(console);
		
		List<Transaction> transactions = transactionsContaining(
				deposit("01/04/2014", 1000),
				withdrawal("02/04/2014", 100),
				deposit("10/04/2014", 500)
				);
		statementPrinter.print(transactions);
		
		InOrder inOrder = inOrder(console);
		inOrder.verify(console).printLine("DATE | AMOUNT | BALANCE");
		inOrder.verify(console).printLine("10/04/2014 | 500,00 | 1400,00");
		inOrder.verify(console).printLine("02/04/2014 | -100,00 | 900,00");
		inOrder.verify(console).printLine("01/04/2014 | 1000,00 | 1000,00");
	}

	private List<Transaction> transactionsContaining(Transaction...transactions) {
		return asList(transactions);
	}

	private Transaction withdrawal(String date, int amount) {
		return new Transaction(date, -amount);
	}

	private Transaction deposit(String date, int amount) {
		return new Transaction(date, amount);
	}
}
