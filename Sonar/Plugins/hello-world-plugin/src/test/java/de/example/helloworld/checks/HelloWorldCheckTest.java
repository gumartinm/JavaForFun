package de.example.helloworld.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class HelloWorldCheckTest {

	@Test
	public void test() {
		JavaCheckVerifier.verify("src/test/files/HelloWorldCheck.java", new HelloWorldCheck());
	}
}
