package org.sosy_lab.pact.model;

/**
 * This stores details about the consumer of the interaction
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#consumer">Pact Consumer</a>
 * @param name the provider name
 */
public record PactConsumer(String name) {}
