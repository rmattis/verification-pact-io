package org.sosy_lab.pact.model;

/**
 * This stores details about the provider of the interaction
 *
 * @see <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#provider">Pact
 *     Provider</a>
 * @param name the provider name
 */
public record PactProvider(String name) {}
