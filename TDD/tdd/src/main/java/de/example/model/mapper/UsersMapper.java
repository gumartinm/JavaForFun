package de.example.model.mapper;

import de.example.model.resource.UserResource;


public interface UsersMapper {

	public UserResource findOne(long id);

	public void create(UserResource user);
}
