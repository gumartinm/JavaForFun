package de.remote.agents.services;

import org.apache.log4j.Logger;

public class WriteTextServiceImpl implements WriteTextService {
    private static final Logger logger = Logger
            .getLogger(WriteTextServiceImpl.class);

    @Override
    public void setWriteText(final String text, final Integer number) {
        logger.info("String received from agent: " + text);
        logger.info("Number received from agent: " + number);
    }

    @Override
    public void setWriteText(final String text) {
        logger.info("We have received one string from agent: " + text);
    }

}
