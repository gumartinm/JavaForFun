package de.example.custom.javascript.checks;

import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.Tree.Kind;
import org.sonar.plugins.javascript.api.tree.expression.CallExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.DotMemberExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.IdentifierTree;
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
		CallExpressionTree callExpression = (CallExpressionTree) tree;
		if (callExpression.callee() instanceof DotMemberExpressionTree) {
			DotMemberExpressionTree callee = (DotMemberExpressionTree) callExpression.callee();
			if (callee.object() instanceof IdentifierTree) {
				IdentifierTree object = (IdentifierTree) callee.object();
				String objectName = object.name();
				String calleeName = callee.property().name();
				if ("$rootScope".equals(objectName) && "$on".equals(calleeName)) {
					addIssue(tree, "Do not use $rootScope.$on because it leaks the Controller instance.");
				}
			}
		}

	}

}
