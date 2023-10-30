package org.sosy_lab.pact.model.matching.rule;

import org.sosy_lab.pact.model.PactMatchingRule;

import java.util.Map;
import java.util.Optional;


/**
 * The matching rules are stored as a key/value map where the key is the category that matchers are applied to.
 * The matching rules supported are body, header, path, query and metadata.
 *
 * @param body     key/value map with the matchingRules for body
 * @param header   key/value map with the matchingRules for header
 * @param path     key/value map with the matchingRules for path
 * @param query    key/value map with the matchingRules for query
 * @param metadata key/value map with the matchingRules for metadata
 */
public record PactMatchingRules(
        Optional<Map<String, PactMatchingRule>> body,
        Optional<Map<String, PactMatchingRule>> header,
        Optional<PactMatchingRule> path,
        Optional<Map<String, PactMatchingRule>> query,
        Optional<Map<String, PactMatchingRule>> metadata
) {
}
