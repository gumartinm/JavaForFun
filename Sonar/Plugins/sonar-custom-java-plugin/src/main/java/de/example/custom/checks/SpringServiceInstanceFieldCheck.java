package de.example.custom.checks;

import java.util.List;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.VariableTree;

import com.google.common.collect.ImmutableList;

@Rule(key = "GU0002")
public class SpringServiceInstanceFieldCheck extends IssuableSubscriptionVisitor {
	private static final Logger LOG = Loggers.get(SpringServiceInstanceFieldCheck.class);
	
	 private JavaFileScannerContext context;


	@Override
	public List<Kind> nodesToVisit() {
		return ImmutableList.of(Kind.CLASS, Kind.VARIABLE);
	}

	@Override
	public void visitNode(Tree tree) {
		if (tree.is(Kind.CLASS) && isSpringService((ClassTree) tree)) {
			
		}
		
	}
	 
	
	  private static boolean isOwnedByASpringService(VariableTree variable) {
		    Symbol owner = variable.symbol().owner();
		    return owner.isTypeSymbol() && (owner.type().isSubtypeOf("javax.servlet.http.HttpServlet") || owner.type().isSubtypeOf("org.apache.struts.action.Action"));
		  }
	  
	  private static boolean isSpringService(ClassTree tree) {
		  tree.symbol().metadata().isAnnotatedWith("javax.inject.Inject");
		  return true;
		  
	 }


}
