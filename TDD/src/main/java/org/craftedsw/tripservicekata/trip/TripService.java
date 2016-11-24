package org.craftedsw.tripservicekata.trip;

import java.util.ArrayList;
import java.util.List;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.User;
import org.craftedsw.tripservicekata.user.UserSession;

public class TripService {

	public List<Trip> getTripsByUser(User user) throws UserNotLoggedInException {
		if (getLoggedInUser() == null) {
			throw new UserNotLoggedInException();
		}
		
		return user.isFriendsWith(getLoggedInUser())
				? tripsBy(user)
			    : new ArrayList<>();
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
