package org.sosy_lab.pact.model.matching.rule.rules;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.sosy_lab.pact.model.matching.rule.PactMatcher;
import org.sosy_lab.pact.model.matching.rule.PactRule;

/**
 * Type matching rule
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#supported-matching-rules">Supported matching rules</a>
 */
@JsonTypeName("type")
public class TypeMatcher extends PactMatcher {
    public TypeMatcher() {
        super(PactRule.Type);
    }
}
