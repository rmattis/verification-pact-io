package org.sosy_lab.pact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Optional;

/**
 * A single interaction between the consumer and provider.
 * For simplicity, we just use the
 * <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#synchronoushttp">Synchronous/HTTP</a>
 * interaction here.
 *
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#consumer">Pact Consumer</a>
 * @param type The type of the interaction (Synchronous/HTTP, Asynchronous/Messages, Synchronous/Messages, etc.)
 * @param description A description for the interaction. Must be unique within the Pact file
 * @param key Unique value for the interaction. Can be auto-generated if not specified.
 * @param response The HTTP request part
 * @param request The HTTP response part
 */
@JsonIgnoreProperties({"providerStates", "pending", "comments", "pluginsConfiguration", "interactionMarkup"})
public record PactInteraction(String type, String description, String key, Optional<PactResponse> response,
                              Optional<PactRequest> request) {
}
