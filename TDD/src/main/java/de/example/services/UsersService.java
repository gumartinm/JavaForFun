package de.example.services;

import de.example.model.resource.UserResource;


public interface UsersService {

	UserResource findOneById(long id);

	void create(UserResource user);
	
	String spiedMethod(UserResource user);
}
