package org.craftedsw.tripservicekata.user;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.craftedsw.tripservicekata.UserBuilder;
import org.junit.Test;

public class UserShould {

	private static final User JOHN = new User();
	private static final User PAUL = new User();

	@Test public void
	inform_when_users_are_not_friends() {
		User user = UserBuilder.aUser()
						.friendsWith(JOHN)
						.build();
		
		assertThat(user.isFriendsWith(PAUL), is(false));
	}
	
	@Test public void
	inform_when_users_are_friends() {
		User user = UserBuilder.aUser()
				.friendsWith(JOHN, PAUL)
				.build();
		
		assertThat(user.isFriendsWith(PAUL), is(true));
	}
}
