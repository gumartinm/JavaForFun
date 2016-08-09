package de.example.custom.java.checks;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;
import org.sonar.java.model.ModifiersUtils;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.VariableTree;

import com.google.common.collect.ImmutableList;

@Rule(key = "GUJ0002")
public class SpringServiceInstanceFieldCheck extends IssuableSubscriptionVisitor {
	private static final Logger LOG = Loggers.get(SpringServiceInstanceFieldCheck.class);

	private final List<VariableTree> issuableVariables = new ArrayList<>();

	private boolean isSpringService = false;

	@Override
	public void scanFile(JavaFileScannerContext context) {
		if (context.getSemanticModel() == null) {
			return;
		}
		super.scanFile(context);

		if (this.isSpringService) {
			reportIssuesOnVariable();
		}
	}

	@Override
	public List<Kind> nodesToVisit() {
		return ImmutableList.of(Kind.CLASS, Kind.VARIABLE);
	}

	@Override
	public void visitNode(Tree tree) {

		if (tree.is(Kind.CLASS) && isSpringService((ClassTree) tree)) {
			this.isSpringService = true;
		} else if (tree.is(Kind.VARIABLE)) {
			VariableTree variable = (VariableTree) tree;
			isOwnedByASpringService(variable);
			if (isOwnedByASpringService(variable) && !isStaticOrFinal(variable)) {
				issuableVariables.add(variable);
			}
		}

	}

	private static boolean isOwnedByASpringService(VariableTree variable) {
		if (variable.symbol().owner().metadata().isAnnotatedWith("javax.inject.Named") ||
				variable.symbol().owner().metadata().isAnnotatedWith("org.springframework.stereotype.Service")) {
			return true;
		}

		return false;
	}

	private static boolean isSpringService(ClassTree tree) {
		if (tree.symbol().metadata().isAnnotatedWith("javax.inject.Named") ||
				tree.symbol().metadata().isAnnotatedWith("org.springframework.stereotype.Service")) {
			return true;
		}

		return false;
	}

	private static boolean isStaticOrFinal(VariableTree variable) {
		ModifiersTree modifiers = variable.modifiers();
		return ModifiersUtils.hasModifier(modifiers, Modifier.STATIC)
				|| ModifiersUtils.hasModifier(modifiers, Modifier.FINAL);
	}

	private void reportIssuesOnVariable() {
		for (VariableTree variable : issuableVariables) {
			reportIssue(variable.simpleName(),
					"Remove this mutable service instance fields or make it \"static\" and/or \"final\"");
		}
		issuableVariables.clear();
	}

}
