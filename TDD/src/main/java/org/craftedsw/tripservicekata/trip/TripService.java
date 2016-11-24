package org.craftedsw.tripservicekata.trip;

import java.util.ArrayList;
import java.util.List;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.User;
import org.craftedsw.tripservicekata.user.UserSession;

public class TripService {

	public List<Trip> getTripsByUser(User user) throws UserNotLoggedInException {
		User loggedInUser = getLoggedInUser();
		if (loggedInUser == null) {
			throw new UserNotLoggedInException();
		}
		
		// Getting rid of variables in legacy code is great because if we do it,
		// it will be easier for us to refactor the code.
		// Once we take away as many variables as posible we can remake optimizations
		// but not before.
		// We managed to get a symmetrical if (ternary operation)
		if (user.isFriendsWith(loggedInUser)) {
			return tripsBy(user);
		} else {
			return new ArrayList<>();
		}
	}

	protected List<Trip> tripsBy(User user) {
		List<Trip> tripList;
		tripList = TripDAO.findTripsByUser(user);
		return tripList;
	}

	protected User getLoggedInUser() {
		User loggedUser = UserSession.getInstance().getLoggedUser();
		return loggedUser;
	}
	
}
