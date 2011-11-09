package de.spring.example;

import de.spring.example.annotation.CustomTransactional;


public class Test {
  public void bar(){
    System.out.println("This is the containing class");
  }
  
  @CustomTransactional
  public class InnerService {
      public void innerMethod() {
          System.out.println("I am the inner class");
      }
  }
}

