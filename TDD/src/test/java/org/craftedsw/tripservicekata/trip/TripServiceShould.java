package org.craftedsw.tripservicekata.trip;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.User;
import org.junit.Test;

public class TripServiceShould {
	
	private static final User GUEST = null;
	private static final User UNUSED_USER = null;
	private User loggedInUser;

	@Test(expected=UserNotLoggedInException.class) public void
	throw_an_exception_when_user_is_not_logged_in() {
		TripService tripService = new TesteableTripService();
		
		loggedInUser = GUEST;
		
		tripService.getTripsByUser(UNUSED_USER);
	}
	
	private class TesteableTripService extends TripService {

		@Override
		protected User getLoggedInUser() {
			return loggedInUser;
		}
		
	}
}
