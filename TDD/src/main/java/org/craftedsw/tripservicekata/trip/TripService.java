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
			    : noTrips();
	}

	private ArrayList<Trip> noTrips() {
		return new ArrayList<>();
	}

	protected List<Trip> tripsBy(User user) {
		List<Trip> tripList;
		tripList = TripDAO.findTripsByUser(user);
		return tripList;
	}

	// In MVC the Model layer should know nothing about the view.
	// Service is in Model layer, so we have to get rid of this method.
	protected User getLoggedInUser() {
		User loggedUser = UserSession.getInstance().getLoggedUser();
		return loggedUser;
	}
	
}
