package de.spring.example.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

@Aspect
// When weaving order is given by @DeclarePreceden annotation
@DeclarePrecedence("BeforeMyAspect, MyAspect")
public class MyAspectsOrder {

}
