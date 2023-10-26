package org.sosy_lab.pact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Pact specification file format
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#file-format">Pact Specification File</a>
 * @param consumer Details about the consumer of the interaction
 * @param provider Details about the provider of the interaction
 * @param interactions All different types of interactions in the contract.
 */
@JsonIgnoreProperties({"metadata"})
public record PactSpecification(PactConsumer consumer, PactProvider provider, List<PactInteraction> interactions) { }
