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
	private static final Trip TO_BERLIN = new Trip();
	private User loggedInUser;
	private TripService tripService;
	
	@Before
	public void setUp() {
		tripService = new TesteableTripService();
		loggedInUser = REGISTERED_USER;
	}

	@Test(expected=UserNotLoggedInException.class) public void
	throw_an_exception_when_user_is_not_logged_in() {
		loggedInUser = GUEST;
		
		tripService.getTripsByUser(UNUSED_USER);
	}
	
	@Test public void
	not_return_any_trips_when_users_are_not_friends() {	
		User friend = UserBuilder.aUser()
						.friendsWith(ANOTHER_USER)
						.withTrips(TO_BRAZIL)
						.build();
		
		List<Trip> friendTrips = tripService.getTripsByUser(friend); 
		// You must always begin writing the assert.
		// Remember: the assert must match the unit test method's name!!
		// In this case, no trips must be returned.
		assertThat(friendTrips.size(), is(0));
	}
	
	@Test public void
	return_friend_trips_when_users_are_friends() {
		User friend = UserBuilder.aUser()
						.friendsWith(ANOTHER_USER, loggedInUser)
						.withTrips(TO_BRAZIL, TO_BERLIN)
						.build();
		
		List<Trip> friendTrips = tripService.getTripsByUser(friend); 
		// You must always begin writing the assert.
		// Remember: the assert must match the unit test method's name!!
		// In this case, no trips must be returned.
		assertThat(friendTrips.size(), is(2));
	}
	
	
	public static class UserBuilder {
		private User[] friends = new User[]{};
		private Trip[] trips = new Trip[]{};
		
		public static UserBuilder aUser() {
			return new UserBuilder();
		}

		public UserBuilder withTrips(Trip...trips) {
			this.trips  = trips;
			return this;
		}

		public UserBuilder friendsWith(User...friends) {
			this.friends = friends;
			return this;
		}
		
		public User build() {
			User user = new User();
			addTripsTo(user);
			addFriendsTo(user);
			return user;
		}

		private void addFriendsTo(User user) {
			for (User friend : friends) {
				user.addFriend(friend);
			}		
		}

		private void addTripsTo(User user) {
			for (Trip trip : trips) {
				user.addTrip(trip);
			}
		}
	}
	
	private class TesteableTripService extends TripService {

		@Override
		protected User getLoggedInUser() {
			return loggedInUser;
		}

		@Override
		protected List<Trip> tripsBy(User user) {
			return user.trips();
		}
		
	}
}
