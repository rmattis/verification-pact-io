package org.sosy_lab.pact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sosy_lab.pact.model.matching.rule.PactMatchingRules;

import java.util.Optional;

/**
 * Represents the response part of an HTTP request in the pact specification file
 *
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#response">Pact
 *     Response Specification</a>
 * @param status The HTTP status code (100-599)
 * @param body Response body
 * @param matchingRules Optional matching rules to apply to the response
 */
@JsonIgnoreProperties({"headers", "header", "generators"})
public record PactResponse(int status, PactBody body, Optional<PactMatchingRules> matchingRules) {}
