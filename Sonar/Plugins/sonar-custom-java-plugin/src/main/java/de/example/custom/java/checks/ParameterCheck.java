package de.example.custom.java.checks;

import java.util.List;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol.MethodSymbol;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import com.google.common.collect.ImmutableList;

@Rule(key = "GU0001")
public class ParameterCheck extends IssuableSubscriptionVisitor {
	private static final Logger LOG = Loggers.get(ParameterCheck.class);


	@Override
	public List<Kind> nodesToVisit() {
		return ImmutableList.of(Kind.METHOD);
	}

	@Override
	public void visitNode(Tree tree) {
		LOG.info("Visiting Node");

		MethodTree method = (MethodTree) tree;
		
		if (method.parameters().size() == 1) {
			MethodSymbol symbol = method.symbol();
			Type firstParameterType = symbol.parameterTypes().get(0);
			Type returnType = symbol.returnType().type();
			if(returnType.is(firstParameterType.fullyQualifiedName())) {
				reportIssue(method.simpleName(), "Never do that!");
			}
		}
		
	}
}
