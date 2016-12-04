package org.craftedsw.feature;

import java.util.List;

public class StatementPrinter {

	private static final String STATEMENT_HEADER = "DATE | AMOUNT | BALANCE";
	
	private final Console console;

	public StatementPrinter(Console console) {
		this.console = console;
	}

	public void print(List<Transaction> transactions) {
		console.printLine(STATEMENT_HEADER);
	}

}
