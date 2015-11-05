package de.example.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.example.model.mapper.UsersMapper;
import de.example.model.resource.UserResource;
import de.example.services.impl.UsersServiceImpl;


public class UsersServiceTest {
	private static final long ID = 10L;
	
	private UsersService usersService;
	private UsersMapper usersMapper;

	@Before
	public void setUp() throws Exception {
		usersMapper = mock(UsersMapper.class);
		// USING spy because we want to spy UsersService.spiedMethod
		// otherwise we wouldn't need org.mockito.Mockito.spy.
		usersService = spy(new UsersServiceImpl(usersMapper));
	}

	@Test
	public void givenSomeIdWhenFindUserThenReturnUser() {
		// Arrange
		UserResource expectedUser = new UserResource(ID, "Gustavo");
		when(usersMapper.findOne(ID)).thenReturn(expectedUser);

		// Act
		UserResource user = usersService.findOneById(ID);
		
		// Assert
		assertEquals(expectedUser, user);
	}

	
	@Test
	public void givenNewUserDataWhenCreateNewUserThenCreateUser() {
		// Arrange
		UserResource expectedUser = new UserResource(ID, "Gustavo");
		UserResource newUser = new UserResource(null, "Gustavo");
		
		// Act
		usersService.create(newUser);
		
		// Assert
		verify(usersService).spiedMethod(newUser);
		verify(usersMapper).create(newUser);
		assertEquals(expectedUser, newUser);		
	}
	
	@Test(expected=NullPointerException.class)
	public void givenNoUserDataWhenCreateNewUserThenThrowNullPointerException() {
		// Act
		usersService.create(null);
	}

	@Test
	public void givenNewUserDataWhenCreateNewUserThenCreateUserWithArgumentCaptor() {
		// Arrange
		UserResource newUser = new UserResource(null, "Gustavo");
		ArgumentCaptor<UserResource> argument = ArgumentCaptor.forClass(UserResource.class);
		
		// Act
		usersService.create(newUser);
		
		// Assert
		verify(usersService).spiedMethod(newUser);
		verify(usersMapper).create(argument.capture());
		assertEquals(argument.getValue(), newUser);		
	}
}
