package de.example.custom.javascript.checks;

import java.io.File;

import org.junit.Test;
import org.sonar.javascript.checks.verifier.JavaScriptCheckVerifier;

public class AngularJSRootOnEventSubscriptionCheckTest {

	  private AngularJSRootOnEventSubscriptionCheck check = new AngularJSRootOnEventSubscriptionCheck();

	  @Test
	  public void testDefault() {
	    JavaScriptCheckVerifier.verify(check, new File("src/test/files/checks/AngularJSRootOnEventSubscriptionCheck.js"));
	  }
}
