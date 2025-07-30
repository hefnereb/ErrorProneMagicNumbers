package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.*;

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

        // Emit a warning for magic numbers
        return buildDescription(literalTree)
            .setMessage("Magic number " + value + " detected; consider replacing with a descriptive constant")
            .build();
    }

}