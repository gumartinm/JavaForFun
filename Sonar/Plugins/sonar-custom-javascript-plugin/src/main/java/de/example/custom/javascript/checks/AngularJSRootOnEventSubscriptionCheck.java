package de.example.custom.javascript.checks;

import java.util.List;

import org.sonar.check.Rule;
import org.sonar.javascript.tree.impl.expression.CallExpressionTreeImpl;
import org.sonar.javascript.tree.impl.expression.DotMemberExpressionTreeImpl;
import org.sonar.javascript.tree.impl.expression.IdentifierTreeImpl;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.Tree.Kind;
import org.sonar.plugins.javascript.api.visitors.SubscriptionVisitorCheck;

import com.google.common.collect.ImmutableList;

@Rule(key = "GUJS0001")
public class AngularJSRootOnEventSubscriptionCheck extends SubscriptionVisitorCheck {
	
	@Override
	public List<Kind> nodesToVisit() {
		return ImmutableList.of(Kind.CALL_EXPRESSION);
	}

	@Override
	public void visitNode(Tree tree) {
		CallExpressionTreeImpl callExpression = (CallExpressionTreeImpl) tree;
		if (callExpression.callee() instanceof DotMemberExpressionTreeImpl) {
			DotMemberExpressionTreeImpl callee = (DotMemberExpressionTreeImpl) callExpression.callee();
			if (callee.object() instanceof IdentifierTreeImpl) {
				IdentifierTreeImpl object = (IdentifierTreeImpl) callee.object();
				String objectName = object.name();
				String calleeName = callee.property().name();
				if ("$rootScope".equals(objectName) && "$on".equals(calleeName)) {
					addIssue(callExpression.getFirstToken(), "Do not use $rootScope.$on because it leaks the Controller instance.");
				}
			}
		}

	}

}
