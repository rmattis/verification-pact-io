package org.sosy_lab.jar.exception;

/**
 * This exception is thrown when the signature of a method couldn't be parsed and we couldn't
 * resolve the correct signature of the method.
 */
public class WrongMethodSignatureException extends RuntimeException {

  public WrongMethodSignatureException(String message, Throwable cause) {
    super(message, cause);
  }
}
