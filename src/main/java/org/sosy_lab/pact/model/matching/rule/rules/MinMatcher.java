package org.sosy_lab.pact.model.matching.rule.rules;

import org.sosy_lab.pact.model.matching.rule.PactMatcher;
import org.sosy_lab.pact.model.matching.rule.PactRule;

/**
 * MinType matching rule
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#supported-matching-rules">Supported matching rules</a>
 */
public class MinMatcher extends PactMatcher {

    private final int min;

    public MinMatcher(int min) {
        super(PactRule.Type);
        this.min = min;
    }

    public int getMin() {
        return min;
    }
}
