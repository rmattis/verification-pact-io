package org.sosy_lab.generation.exception;

/**
 * Exception when we couldn't find a matching constructor that has at least as many arguments as the
 * jsonNode has fields.
 */
public class NoMatchingConstructorException extends RuntimeException {

  public NoMatchingConstructorException(String message) {
    super(message);
  }
}
