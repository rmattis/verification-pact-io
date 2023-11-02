package org.sosy_lab.jar.model;

/**
 * Represents a method parameter of a spring endpoint method.
 *
 * @param name The name of the parameter (e.g. "id")
 * @param type The type of the parameter (e.g. "java.lang.String")
 */
public record SpringMethodParameter(String name, String type) {}
