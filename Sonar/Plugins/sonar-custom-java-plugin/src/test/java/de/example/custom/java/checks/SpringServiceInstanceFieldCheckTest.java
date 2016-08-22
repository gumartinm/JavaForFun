package de.example.custom.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SpringServiceInstanceFieldCheckTest {
	  private static final String NAMED_ANNOTATION = "src/test/files/checks/SpringServiceNamedAnnotationInstanceFieldCheck.java";
	  private static final String SPRING_SERVICE_ANNOTATION = "src/test/files/checks/SpringServiceAnnotationInstanceFieldCheck.java";
	  private static final String NO_SPRING_SERVICE_ANNOTATION = "src/test/files/checks/NoSpringServiceAnnotationInstanceFieldCheck.java";

	  @Test
	  public void whenNamedAnnotationAndNoStaticOrFinalFieldThenGenerateIssues() {
	    JavaCheckVerifier.verify(NAMED_ANNOTATION, new SpringServiceInstanceFieldCheck());
	  }
	  
	  @Test
	  public void whenSpringServiceAnnotationAndNoStaticOrFinalFieldThenGenerateIssues() {
	    JavaCheckVerifier.verify(SPRING_SERVICE_ANNOTATION, new SpringServiceInstanceFieldCheck());
	  }
	  
	  @Test
	  public void whenNoSpringServiceOrNamedAnnotationThenNoIssues() {
	    JavaCheckVerifier.verifyNoIssue(NO_SPRING_SERVICE_ANNOTATION, new SpringServiceInstanceFieldCheck());
	  }
}
