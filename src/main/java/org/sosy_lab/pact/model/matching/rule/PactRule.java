package org.sosy_lab.pact.model.matching.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This enum contains all supported pact matching rules.
 */
public enum PactRule {

    @JsonProperty("equality")
    Equality,
    @JsonProperty("regex")
    Regex,
    @JsonProperty("type")
    Type;
}
