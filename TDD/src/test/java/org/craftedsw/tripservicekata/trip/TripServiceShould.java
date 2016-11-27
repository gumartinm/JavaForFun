package org.craftedsw.tripservicekata.trip;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.craftedsw.tripservicekata.UserBuilder;
import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TripServiceShould {
	
	private static final User GUEST = null;
	private static final User UNUSED_USER = null;
	private static final User REGISTERED_USER = new User();
	private static final User ANOTHER_USER = new User();
	private static final Trip TO_BRAZIL = new Trip();
	private static final Trip TO_BERLIN = new Trip();
	
	@Mock TripDAO tripDAO;
	private TripService realTripService;
	private TripService tripService;
	
	@Before
	public void setUp() {
		tripService = new TesteableTripService();
	}

	@Test(expected=UserNotLoggedInException.class) public void
	throw_an_exception_when_user_is_not_logged_in() {		
		tripService.getTripsByUser(UNUSED_USER, GUEST);
	}
	
	@Test public void
	not_return_any_trips_when_users_are_not_friends() {	
		User friend = UserBuilder.aUser()
						.friendsWith(ANOTHER_USER)
						.withTrips(TO_BRAZIL)
						.build();
		
		List<Trip> friendTrips = tripService.getTripsByUser(friend, REGISTERED_USER); 
		// You must always begin writing the assert.
		// Remember: the assert must match the unit test method's name!!
		// In this case, no trips must be returned.
		assertThat(friendTrips.size(), is(0));
	}
	
	@Test public void
	return_friend_trips_when_users_are_friends() {
		User friend = UserBuilder.aUser()
						.friendsWith(ANOTHER_USER, REGISTERED_USER)
						.withTrips(TO_BRAZIL, TO_BERLIN)
						.build();
		
		List<Trip> friendTrips = tripService.getTripsByUser(friend, REGISTERED_USER); 
		// You must always begin writing the assert.
		// Remember: the assert must match the unit test method's name!!
		// In this case, no trips must be returned.
		assertThat(friendTrips.size(), is(2));
	}
	
	private class TesteableTripService extends TripService {

		@Override
		protected List<Trip> tripsBy(User user) {
			return user.trips();
		}
		
	}
}
