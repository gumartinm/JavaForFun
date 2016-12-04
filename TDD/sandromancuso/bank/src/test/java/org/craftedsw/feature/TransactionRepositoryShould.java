package org.craftedsw.feature;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

// This would be an integration test because underneath there should be
// a database. For the sake of simplicity we will have an in memory
// repository.
@RunWith(MockitoJUnitRunner.class)
public class TransactionRepositoryShould {

	private static final String TODAY = "04/12/2016";

	@Mock private Clock clock;
	
	private TransactionRepository transactionRepository;
	
	@Before
	public void initialise() {
		transactionRepository = new TransactionRepository(clock);
	}

	@Test public void
	create_and_store_a_deposit_transaction() {
		given(clock.todayAsString()).willReturn(TODAY);
		transactionRepository.addDeposit(100);
		
		List<Transaction> transactions = transactionRepository.allTransactions();
		
		assertThat(transactions.size(), is(1));
		assertThat(transactions.get(0), is(transaction(TODAY, 100)));
	}

	private Transaction transaction(String date, int amount) {
		return new Transaction(date, amount);
	}

}
