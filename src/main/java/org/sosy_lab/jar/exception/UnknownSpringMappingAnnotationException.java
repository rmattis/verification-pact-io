package org.sosy_lab.jar.exception;

/** This exception is thrown when we encounter a spring mapping annotation that we don't know. */
public class UnknownSpringMappingAnnotationException extends RuntimeException {

  public UnknownSpringMappingAnnotationException(String message) {
    super(message);
  }

  public UnknownSpringMappingAnnotationException(String message, Throwable cause) {
    super(message, cause);
  }
}
