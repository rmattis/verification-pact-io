package org.sosy_lab.jar.exception;

/** This exception is thrown when we couldn't load a class with javassist from the jarFile. */
public class ClassLoadFailedException extends RuntimeException {

  public ClassLoadFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
