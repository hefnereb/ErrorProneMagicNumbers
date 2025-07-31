package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.LiteralTree;
import com.sun.source.util.TreePath;
import com.sun.source.tree.Tree.Kind;

import java.util.HashMap;
import java.util.Map;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

/**
 * MagicNumberChecker: flags numeric literals (excluding common safe ones) used in
 * expressions like comparisons, arithmetic, conditionals, loops, or array accesses,
 * and suggests extracting them into named constants. It also aggregates usage
 * statistics to help spot patterns.
 */
@AutoService(BugChecker.class)
@BugPattern(
    name = "MagicNumberChecker",
    summary = "Magic number usage; extract numeric literals into named constants",
    severity = WARNING,
    linkType = CUSTOM,
    link = "https://github.com/hefnereb/ErrorProneMagicNumbers.git"
)
public class MagicNumberChecker extends BugChecker implements BugChecker.LiteralTreeMatcher {

    private static final Map<Double, Integer> numberCounts = new HashMap<>(); 

    private static final Map<Double, Map<Kind, Integer>> numberContexts = new HashMap<>(); 

    private static final Map<Kind, Integer> contextTotals = new HashMap<>(); 

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("=== MagicNumberChecker Summary ===");
            numberCounts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(entry -> {
                    double num = entry.getKey();
                    int count = entry.getValue();
                    System.err.printf("Number %s: %d occurrences%n", num, count);
                    Map<Kind, Integer> ctx = numberContexts.getOrDefault(num, Map.of());
                    for (Map.Entry<Kind, Integer> ctxEntry : ctx.entrySet()) {
                        System.err.printf("     %s uses: %d%n", ctxEntry.getKey(), ctxEntry.getValue());
                    }
                });
            System.err.println("==================================");
        }));
    }

    @Override
    public Description matchLiteral(LiteralTree literalTree, VisitorState state) {
        Object value = literalTree.getValue();
        if (!(value instanceof Number)) {
            return Description.NO_MATCH;
        }
        double numericValue = ((Number) value).doubleValue();
        if (numericValue == 0.0 || numericValue == 1.0) {
            return Description.NO_MATCH;
        }

        Kind contextKind = getRelevantContextKind(state.getPath());
        if (contextKind == null) {
            return Description.NO_MATCH; 
        }

        numberCounts.put(numericValue, numberCounts.getOrDefault(numericValue, 0) + 1);

        numberContexts.computeIfAbsent(numericValue, k -> new HashMap<>())
            .merge(contextKind, 1, Integer::sum);

        contextTotals.put(contextKind, contextTotals.getOrDefault(contextKind, 0) + 1);

        return buildDescription(literalTree)
            .setMessage("Magic number " + value + " detected in " + contextKind
                + "; consider replacing with a descriptive constant")
            .build();
    }

    private Kind getRelevantContextKind(TreePath path) {
        TreePath parent = path.getParentPath();
        while (parent != null) {
            Kind k = parent.getLeaf().getKind();
            switch (k) {
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
                    return k; 
                default:
                    break;
            }
            parent = parent.getParentPath();
        }
        return null; 
    }
}