package de.example.custom.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SpringServiceInstanceFieldCheckTest {
	  private static final String FILENAME = "src/test/files/checks/SpringServiceInstanceFieldCheck.java";

	  //@Test
	  public void test() {
	    JavaCheckVerifier.verify(FILENAME, new SpringServiceInstanceFieldCheck());
	  }
}
