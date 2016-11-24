package org.craftedsw.tripservicekata.trip;

import java.util.ArrayList;
import java.util.List;

import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
import org.craftedsw.tripservicekata.user.User;
import org.craftedsw.tripservicekata.user.UserSession;

public class TripService {

	public List<Trip> getTripsByUser(User user) throws UserNotLoggedInException {
		List<Trip> tripList = new ArrayList<Trip>();
		// In Unit Test we shouldn't invoke other classes because
		// other classes could be using data base, network, etc, etc.
		// User loggedUser = UserSession.getInstance().getLoggedUser();
		User loggedUser = getLoggedInUser();
		boolean isFriend = false;
		if (loggedUser != null) {
			// Feature envy. TripService envies User class. This should be done by User class.
			for (User friend : user.getFriends()) {
				if (friend.equals(loggedUser)) {
					isFriend = true;
					break;
				}
			}
			// The deepest branch. For refactoring legacy code we must begin from the
			// deepest branch. This is just the opposite for creating the unit test
			// for our legacy code.
			// 1. Write the unit test from the shortest to deepest branch. Modifications
			// in legacy code must be done JUST with the automatic tools provided by the IDE.
			// 2. Once the legacy code is under test start refactoring from deepest
			// to shortest branch. Modifications in the legacy code may be hand made.
			// In this case we can not do anything with this code (this is the deepest branch)
			// so we will have to start with the for loop (which is the second deepest branch)
			if (isFriend) {
				tripList = tripsBy(user);
			}
			return tripList;
		} else {
			throw new UserNotLoggedInException();
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
