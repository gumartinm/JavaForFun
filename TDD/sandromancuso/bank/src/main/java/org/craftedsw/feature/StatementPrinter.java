package org.craftedsw.feature;

import java.util.List;

public class StatementPrinter {

	private final Console console;

	public StatementPrinter(Console console) {
		this.console = console;
	}

	public void print(List<Transaction> transactions) {
		console.printLine("DATE | AMOUNT | BALANCE");
	}

}
