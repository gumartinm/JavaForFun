package de.remote.agents.services;

import org.apache.log4j.Logger;

import de.remote.agents.SnakeEyes;
import de.remote.agents.SnakeEyesImpl;

public class UserServiceImpl implements UserService {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Override
    public SnakeEyes createUser(final String canon, final String name) {
        logger.info("Canon: " + canon);
        logger.info("Name: " + name);

        // TODO: spring factory :(
        return new SnakeEyesImpl(canon, name);
    }

}
