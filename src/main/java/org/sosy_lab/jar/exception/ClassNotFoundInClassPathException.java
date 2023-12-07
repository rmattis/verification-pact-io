package org.sosy_lab.jar.exception;

/**
 * Exception that appears when a class that was found in a method signature couldn't be found in the
 * classpath.
 */
public class ClassNotFoundInClassPathException extends RuntimeException {

  public ClassNotFoundInClassPathException(String message, Throwable cause) {
    super(message, cause);
  }
}
