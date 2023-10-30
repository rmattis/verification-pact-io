package org.sosy_lab.pact.model.matching.rule.rules;

import org.sosy_lab.pact.model.matching.rule.PactMatcher;
import org.sosy_lab.pact.model.matching.rule.PactRule;

/**
 * Equality matching rule
 *
 * @see <a
 *     href="https://github.com/pact-foundation/pact-specification/tree/version-4#supported-matching-rules">Supported
 *     matching rules</a>
 */
public class EqualityMatcher extends PactMatcher {
  public EqualityMatcher() {
    super(PactRule.Equality);
  }
}
