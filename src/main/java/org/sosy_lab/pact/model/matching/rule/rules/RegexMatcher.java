package org.sosy_lab.pact.model.matching.rule.rules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.sosy_lab.pact.model.matching.rule.PactMatcher;
import org.sosy_lab.pact.model.matching.rule.PactRule;

/**
 * Regex matching rule
 *
 * @see <a
 *     href="https://github.com/pact-foundation/pact-specification/tree/version-4#supported-matching-rules">Supported
 *     matching rules</a>
 */
public class RegexMatcher extends PactMatcher {

  private final String regex;

  @JsonCreator
  public RegexMatcher(@JsonProperty("regex") String regex) {
    super(PactRule.Regex);
    this.regex = regex;
  }
}
