package de.example.custom.java.checks;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.java.model.ModifiersUtils;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.VariableTree;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

@Rule(key = "GUJ0002")
public class SpringServiceInstanceFieldCheck extends IssuableSubscriptionVisitor {
	private static final Logger LOG = Loggers.get(SpringServiceInstanceFieldCheck.class);
	private static final Splitter ANNOTATIONS_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();
	private static final String DEFAULT = "javax.inject.Named, org.springframework.stereotype.Service";

	private final List<VariableTree> issuableVariables = new ArrayList<>();

	private boolean isSpringService = false;
	
	@RuleProperty(
		key = "annotations",
		description = "Annotations to be checked. Multiple values comma separated.",
		defaultValue = "" + DEFAULT)
	public String annotations = DEFAULT;

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

	private boolean isOwnedByASpringService(VariableTree variable) {
		List<String> annotationsToBeChecked = ANNOTATIONS_SPLITTER.splitToList(annotations);
		for (String annotation : annotationsToBeChecked) {
			if (variable.symbol().owner().metadata().isAnnotatedWith(annotation)) {
				return true;
			}
		}

		return false;
	}

	private boolean isSpringService(ClassTree tree) {
		List<String> annotationsToBeChecked = ANNOTATIONS_SPLITTER.splitToList(annotations);
		for (String annotation : annotationsToBeChecked) {
			if (tree.symbol().metadata().isAnnotatedWith(annotation)) {
				return true;
			}
		}

		return false;
	}

	private boolean isStaticOrFinal(VariableTree variable) {
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
