package de.example.custom.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class ParameterCheckTest {

	@Test
	public void test() {
		JavaCheckVerifier.verify("src/test/files/checks/ParameterCheck.java", new ParameterCheck());
	}
}
