package de.example.custom.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SpringServiceInstanceFieldCheckTest {
	  private static final String NAMED_ANNOTATION = "src/test/files/checks/SpringServiceNamedAnnotationInstanceFieldCheck.java";
	  private static final String SPRING_SERVICE_ANNOTATION = "src/test/files/checks/SpringServiceAnnotationInstanceFieldCheck.java";

	  @Test
	  public void whenNamedAnnotationAndNoStaticOrFinalField() {
	    JavaCheckVerifier.verify(NAMED_ANNOTATION, new SpringServiceInstanceFieldCheck());
	  }
	  
	  @Test
	  public void whenSpringServiceAnnotationAndNoStaticOrFinalField() {
	    JavaCheckVerifier.verify(SPRING_SERVICE_ANNOTATION, new SpringServiceInstanceFieldCheck());
	  }
}
