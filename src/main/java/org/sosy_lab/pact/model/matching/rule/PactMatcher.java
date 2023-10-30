package org.sosy_lab.pact.model.matching.rule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.sosy_lab.pact.model.matching.rule.rules.MinMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.MaxMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.MinMaxMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.EqualityMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.RegexMatcher;

/**
 * Abstract class for pact matchers from matching rules.
 *
 * @see <a
 *     href="https://github.com/pact-foundation/pact-specification/tree/version-4#supported-matching-rules">Supported
 *     matching rules</a>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "match")
@JsonSubTypes({
  @JsonSubTypes.Type(value = MaxMatcher.class, name = "type"),
  @JsonSubTypes.Type(value = MinMatcher.class, name = "type"),
  @JsonSubTypes.Type(value = MinMaxMatcher.class, name = "type"),
  @JsonSubTypes.Type(value = EqualityMatcher.class, name = "equality"),
  @JsonSubTypes.Type(value = RegexMatcher.class, name = "regex")
})
public abstract class PactMatcher {

  private final PactRule match;

  public PactMatcher(PactRule match) {
    this.match = match;
  }

  @JsonIgnore
  public PactRule getMatch() {
    return match;
  }
}
