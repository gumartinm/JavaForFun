package org.craftedsw.tripservicekata.trip;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.User;
import org.junit.Before;
import org.junit.Test;

public class TripServiceShould {
	
	private static final User GUEST = null;
	private static final User UNUSED_USER = null;
	private static final User REGISTERED_USER = new User();
	private static final User ANOTHER_USER = new User();
	private static final Trip TO_BRAZIL = new Trip();
	private User loggedInUser;
	private TripService tripService;
	
	@Before
	public void setUp() {
		tripService = new TesteableTripService();
	}

	@Test(expected=UserNotLoggedInException.class) public void
	throw_an_exception_when_user_is_not_logged_in() {
		loggedInUser = GUEST;
		
		tripService.getTripsByUser(UNUSED_USER);
	}
	
	@Test public void
	not_return_any_trips_when_users_are_not_friends() {		
		loggedInUser = REGISTERED_USER;
		
		User friend = new User();
		friend.addFriend(ANOTHER_USER);
		friend.addTrip(TO_BRAZIL);
		
		List<Trip> friendTrips = tripService.getTripsByUser(friend); 
		// You must always begin writing the assert.
		// Remember: the assert must match the unit test method's name!!
		// In this case, no trips must be returned.
		assertThat(friendTrips.size(), is(0));
	}
	
	private class TesteableTripService extends TripService {

		@Override
		protected User getLoggedInUser() {
			return loggedInUser;
		}
		
	}
}
