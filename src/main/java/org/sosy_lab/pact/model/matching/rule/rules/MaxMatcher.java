package org.sosy_lab.pact.model.matching.rule.rules;

import org.sosy_lab.pact.model.matching.rule.PactMatcher;
import org.sosy_lab.pact.model.matching.rule.PactRule;

/**
 * MaxType matching rule
 *
 * @see <a
 *     href="https://github.com/pact-foundation/pact-specification/tree/version-4#supported-matching-rules">Supported
 *     matching rules</a>
 */
public class MaxMatcher extends PactMatcher {

  private final int max;

  public MaxMatcher(int max) {
    super(PactRule.Type);
    this.max = max;
  }

  public int getMax() {
    return max;
  }
}
