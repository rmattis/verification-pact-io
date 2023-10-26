package org.sosy_lab.pact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.sosy_lab.pact.model.matching.rule.PactMatchingRules;

import java.util.Optional;

/**
 * Represents the request part of an HTTP request in the pact specification file
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#request">Pact Request Specification</a>
 * @param method The HTTP method
 * @param path Request path
 * @param body Request body
 * @param matchingRules Optional matching rules to apply to the response
 */
@JsonIgnoreProperties({"query", "headers", "generators"})
public record PactRequest(String method, String path, JsonNode body, Optional<PactMatchingRules> matchingRules) {}
