package org.sosy_lab.pact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Request/Response bodies and message contents are represented with an entity with the following attributes:
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#response">Pact Response Specification</a>
 * @param contentType The content type of the body from the
 *                    <a href="https://www.iana.org/assignments/media-types/media-types.xhtml">IANA registry</a>
 * @param encoded If the body has been encoded (for example, with base64), the encoding used. Otherwise, false.
 *                Note for JSON stored in string form, encoded should be JSON.
 * @param content If encoded, must be a string value. Otherwise, can be any JSON.
 */
@JsonIgnoreProperties({"contentTypeHint"})
public record PactBody(String contentType, JsonNode encoded, JsonNode content) {
}
