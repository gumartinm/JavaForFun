package de.example.services.impl;

import de.example.model.mapper.UsersMapper;
import de.example.model.resource.UserResource;
import de.example.services.UsersService;


public class UsersServiceImpl implements UsersService {
	private final UsersMapper usersMapper;
	
	public UsersServiceImpl(final UsersMapper usersMapper) {
		this.usersMapper = usersMapper;
	}
	
	@Override
	public UserResource findOneById(long id) {
	    return this.usersMapper.findOne(id);
	    
    }

	@Override
    public void create(final UserResource user) {
		this.spiedMethod(user);
		
		usersMapper.create(user); 
    }
	
	@Override
    public String spiedMethod(final UserResource user) {
		return user.getName();
	}

}
