package org.sosy_lab.pact.model.matching.rule.rules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sosy_lab.pact.model.matching.rule.PactMatcher;
import org.sosy_lab.pact.model.matching.rule.PactRule;

import java.util.Optional;

/**
 * MinMaxType matching rule
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#supported-matching-rules">Supported matching rules</a>
 */
public class MinMaxMatcher extends PactMatcher {

    private final Optional<Integer> min;
    private final Optional<Integer> max;
    @JsonCreator
    public MinMaxMatcher(@JsonProperty("min") Integer min, @JsonProperty("max") Integer max) {
        super(PactRule.Type);

        this.min = min == null ? Optional.empty() : Optional.of(min);
        this.max = max == null ? Optional.empty() : Optional.of(max);
    }

    public Optional<Integer> getMin() {
        return min;
    }

    public Optional<Integer> getMax() {
        return max;
    }
}
