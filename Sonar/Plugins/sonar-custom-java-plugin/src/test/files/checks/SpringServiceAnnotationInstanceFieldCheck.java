package org.springframework.stereotype;

public @interface Service {
	String value() default "";
}


@Service("aService")
public class AService {
	private static final Integer FIELD1;
	
	private final Integer field2;
	
	private Integer field3;  // Noncompliant {{Remove this mutable service instance fields or make it "static" and/or "final"}}
	
	public static final Integer field4;
	
	public final Integer field5;
	
	public Integer field6;  // Noncompliant {{Remove this mutable service instance fields or make it "static" and/or "final"}}

}
