package de.example.mybatis.spring;

import org.apache.log4j.Logger;

import de.example.mybatis.spring.service.ExampleService;

public class TestMain {
    private static final Logger logger = Logger.getLogger(TestMain.class);

    public static void main(final String[] args) {

        logger.info("Starting application");

        final ExampleService exampleService = (ExampleService) SpringContextLocator
                .getInstance().getBean("exampleService");

        exampleService.getAdsByCriteria();
    }

}
