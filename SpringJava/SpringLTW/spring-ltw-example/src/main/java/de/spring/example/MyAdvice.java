package de.spring.example;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import de.spring.example.TransactionManagerN2A;

@Aspect
public class MyAdvice {
	
	
	@Before("@annotation(es.dia.pos.n2a.spring.example.annotation.initTransactionalN2A)")
	public void initTransactionalN2A()
	{
		System.out.println("I am the Advice initTransaction.");
		TransactionManagerN2A.getInstance().initTransaction();
	}
	

	@After("@annotation(es.dia.pos.n2a.spring.example.annotation.commitTransactionalN2A)")
	public void commitTransactionalN2A() {
		System.out.println("I am the Advice commitTransaction.");
		TransactionManagerN2A.getInstance().commitTransaction();	
	}
}
