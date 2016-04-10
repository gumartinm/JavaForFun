package de.example.mybatis.spring;

import de.example.mybatis.spring.service.ExampleService;

public class TestMain {
    public static void main(final String[] args) {
        
        final ExampleService example = (ExampleService) SpringContextLocator
                .getInstance().getBean("exampleService");
        
        
        example.insertAndUpdateAds();
        
    }

}
