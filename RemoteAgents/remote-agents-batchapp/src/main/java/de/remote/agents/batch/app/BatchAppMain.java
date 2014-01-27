package de.remote.agents.batch.app;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

public class BatchAppMain {
    private static final Logger logger = Logger.getLogger(BatchAppMain.class);

    @PostConstruct
    public void batchAppMain() {
        logger.info("GI JOE!");
    }
}
