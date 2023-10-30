package org.sosy_lab.pact.model;

import org.sosy_lab.pact.model.matching.rule.PactMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.MaxMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.MinMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.MinMaxMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.TypeMatcher;

import java.util.List;
import java.util.Optional;

/**
 * A single pact matching rule.
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#matching-rules>Matching Rule</a>
 * @param combine Optional. Whether the results of applying the matching rules should be combined using an AND or OR operation.
 * @param matchers List of matching rules.
 */
public record PactMatchingRule(Optional<String> combine, List<PactMatcher> matchers) {

    public PactMatchingRule(Optional<String> combine, List<PactMatcher> matchers) {
        this.combine = combine;
        // We need to manually override the MinMaxMatchers where max, min or both are empty
        // as Jackson can't handle having multiple properties with the same value for JsonSubTypes.
        this.matchers = matchers.stream().map(matcher -> switch (matcher) {
            case MinMaxMatcher minMaxMatcher when minMaxMatcher.getMax().isEmpty() && minMaxMatcher.getMin().isEmpty() ->
                    new TypeMatcher();
            case MinMaxMatcher minMaxMatcher when minMaxMatcher.getMax().isEmpty() ->
                    new MinMatcher(minMaxMatcher.getMin().get());
            case MinMaxMatcher minMaxMatcher when minMaxMatcher.getMin().isEmpty() ->
                    new MaxMatcher(minMaxMatcher.getMax().get());
            default -> matcher;
        }).toList();
    }
}
