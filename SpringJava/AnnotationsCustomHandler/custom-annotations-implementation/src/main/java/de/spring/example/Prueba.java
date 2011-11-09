package de.spring.example;

import es.dia.pos.n2a.aspectj.annotations.TransactionalN2A;


public class Prueba {
  public void bar(){
    System.out.println("I am not a number, I am a free man!");
  }
  
  @TransactionalN2A
  public class InnerService {
      public void innerMethod() {
          System.out.println("xxx: AopService$InnerClass.innerMethod()");
      }
  }

}

