package de.example.custom.javascript.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.javascript.api.visitors.DoubleDispatchVisitorCheck;

@Rule(key = "GUJS0001")
public class AngularJSRootOnEventSubscriptionCheck extends DoubleDispatchVisitorCheck {

}
