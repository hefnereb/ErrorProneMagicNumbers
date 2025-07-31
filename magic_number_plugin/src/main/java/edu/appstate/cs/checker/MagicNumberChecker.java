package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.LiteralTree;
import com.sun.source.util.TreePath;


import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

@AutoService(BugChecker.class)
@BugPattern(
    name = "MagicNumberChecker",
    summary = "Magic number usage; extract numeric literals into named constants",
    severity = WARNING,
    linkType = CUSTOM,
    link = "https://github.com/hefnereb/ErrorProneMagicNumbers.git"
)
public class MagicNumberChecker extends BugChecker implements BugChecker.LiteralTreeMatcher {

    @Override
    public Description matchLiteral(LiteralTree literalTree, VisitorState state) {
        Object value = literalTree.getValue();
        // Only consider numeric literals
        if (!(value instanceof Number)) {
            return Description.NO_MATCH;
        }
        double numericValue = ((Number) value).doubleValue();
        // Exclude common safe values
        if (numericValue == 0.0 || numericValue == 1.0) {
            return Description.NO_MATCH;
        }

         // Only flag when used in arithmetic, conditionals, or array indexing
         if (!inRelevantContext(state.getPath())) {
            return Description.NO_MATCH;
         }

        // Emit a warning for magic numbers
        return buildDescription(literalTree)
            .setMessage("Magic number " + value + " detected; consider replacing with a descriptive constant")
            .build();
    }

     private boolean inRelevantContext(TreePath path) {
        TreePath parent = path.getParentPath();
        while (parent != null) {
            switch (parent.getLeaf().getKind()) {
                case PLUS:
                case MINUS:
                case MULTIPLY:
                case DIVIDE:
                case REMAINDER:
                case CONDITIONAL_AND:
                case CONDITIONAL_OR:
                case LESS_THAN:
                case GREATER_THAN:
                case LESS_THAN_EQUAL:
                case GREATER_THAN_EQUAL:
                case EQUAL_TO:
                case NOT_EQUAL_TO:
                case ARRAY_ACCESS:
                case IF:
                case WHILE_LOOP:
                case DO_WHILE_LOOP:
                    return true;
                default:
                    break;
            }
            parent = parent.getParentPath();
        }
        return false;
    }

}