package de.sql.tests;

import de.sql.tests.springtransaction.TransactionExample;

public class SpringSQLExample {


    public static void main(final String[] args) {

        final TransactionExample test =
                (TransactionExample) SpringContextLocator.getInstance().getBean("transactionExample");
        test.doExample();

    }
}
