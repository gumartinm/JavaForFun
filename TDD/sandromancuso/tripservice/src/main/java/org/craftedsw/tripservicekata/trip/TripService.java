package org.craftedsw.tripservicekata.trip;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.User;

public class TripService {
	
	private final TripDAO tripDAO;

	@Inject
	public TripService(TripDAO tripDAO) {
		this.tripDAO = tripDAO;
	}

	public List<Trip> getTripsByUser(User user, User loggedInUser) throws UserNotLoggedInException {
		validate(loggedInUser);
		
		return user.isFriendsWith(loggedInUser)
				? tripsBy(user)
			    : noTrips();
	}

	private void validate(User loggedInUser) {
		if (loggedInUser == null) {
			throw new UserNotLoggedInException();
		}
	}

	private ArrayList<Trip> noTrips() {
		return new ArrayList<>();
	}

	private List<Trip> tripsBy(User user) {
		return tripDAO.tripsBy(user);
	}
}
