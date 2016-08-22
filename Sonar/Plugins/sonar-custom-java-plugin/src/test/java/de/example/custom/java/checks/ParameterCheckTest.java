package de.example.custom.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class ParameterCheckTest {

	@Test
	public void whenCheckingFunctionParametersThenGenerateIssues() {
		JavaCheckVerifier.verify("src/test/files/checks/ParameterCheck.java", new ParameterCheck());
	}
}
