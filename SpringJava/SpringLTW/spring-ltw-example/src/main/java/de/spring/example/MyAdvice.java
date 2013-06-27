package de.spring.example;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class MyAdvice {


    @Before("execution(* de.spring.example.web.Gus.prueba())")
    public void initTransactional()
    {
        System.out.println("I am the Advice initTransaction.");
        TransactionManager.getInstance().initTransaction();
    }


    @After("@annotation(de.spring.example.annotation.commitTransactional)")
    public void commitTransactional() {
        System.out.println("I am the Advice commitTransaction.");
        TransactionManager.getInstance().commitTransaction();
    }
}
