package org.craftedsw.feature;

import static org.junit.Assert.*;

import org.junit.Test;

public class PrintStatementFeature {

	@Test public void
	print_statement_containing_all_transactions() {
		
		
		// console will be some kind of interface that will represent my class console
		// (always using interfaces for external stuff, like databases, etc, etc)
		verify(console).printLine("DATE | AMOUNT | BALANCE");
		verify(console).printLine("10 / 04 / 2014 | 500.00 | 1400.00");
		verify(console).printLine("02 / 04 / 2014 | -100.00 | 900.00");
		verify(console).printLine("01 / 04 / 2014 | 1000.00 | 1000.00");
	}

}
