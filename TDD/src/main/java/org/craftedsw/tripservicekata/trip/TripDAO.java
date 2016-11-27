package org.craftedsw.tripservicekata.trip;

import java.util.List;

import org.craftedsw.tripservicekata.exception.CollaboratorCallException;
import org.craftedsw.tripservicekata.user.User;

public class TripDAO {

	public static List<Trip> findTripsByUser(User user) {
		throw new CollaboratorCallException(
				"TripDAO should not be invoked on an unit test.");
	}

	// At the end and by means of many code refactoring our app
	// will end up using the instance method. In the meanwhile we
	// will not be able to remove the static method but at least
	// we are offering something that can be tested.
	// Be careful when refactoring this code because in real life
	// findTripsByUser for sure will be used in many places but again
	// in the meanwhile with this code we can write unit tests (we
	// always need to write a test of our legacy code before refactoring it)
	public List<Trip> tripsBy(User user) {
		return TripDAO.findTripsByUser(user);
	}
	
}