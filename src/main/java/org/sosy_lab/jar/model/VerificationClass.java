package org.sosy_lab.jar.model;

import java.util.List;
import java.util.Optional;

/**
 * Represents a class that was used in some way in a spring endpoint method. This can be as request
 * parameter or in the response. This is some kind of wrapper and ease for the @see
 * javassist.CtClass java class.
 */
public class VerificationClass {

  private final String simpleName;
  private final String packageName;
  private final Optional<String> signature;
  private final List<ClassConstructor> constructors;
  private final boolean isInterface;

  /**
   * Represents a class that was used in some way in a spring endpoint method. This can be as
   * request parameter or in the response
   *
   * @param simpleName The name of the class
   * @param packageName The package name of the class
   * @param signature Optional signature of the class
   * @param constructors The constructors of the class
   * @param isInterface Whether this class is an interface or not
   */
  public VerificationClass(
      String simpleName,
      String packageName,
      Optional<String> signature,
      List<ClassConstructor> constructors,
      boolean isInterface) {
    this.simpleName = simpleName;
    this.packageName = packageName;
    this.signature = signature;
    this.constructors = constructors;
    this.isInterface = isInterface;
  }

  /** Returns the simple name of this class (e.g. "ProductController") */
  public String getSimpleName() {
    return simpleName;
  }

  /** Returns the package name of this class (e.g. "au.com.dius.pactworkshop.provider") */
  public String getPackageName() {
    return packageName;
  }

  /** Returns an optional signature for this class. */
  public Optional<String> getSignature() {
    return signature;
  }

  /** Returns all constructors of this class. */
  public List<ClassConstructor> getConstructors() {
    return constructors;
  }

  /** Whether this class is an interface or not. */
  public boolean isInterface() {
    return isInterface;
  }
}
