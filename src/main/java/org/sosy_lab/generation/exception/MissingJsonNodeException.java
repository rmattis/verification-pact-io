package org.sosy_lab.generation.exception;

/**
 * Exception thrown when we couldn't find a jsonNode in the json that matches the name of the
 * parameter in the constructor.
 */
public class MissingJsonNodeException extends RuntimeException {

  public MissingJsonNodeException(String message) {
    super(message);
  }
}
