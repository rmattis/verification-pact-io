package org.sosy_lab.jar.model;

import java.util.List;
import java.util.Optional;

/**
 * Special case of a verification class. This class is used when a list is used in a spring
 * endpoint.
 */
public class VerificationListClass extends VerificationClass {

  private final VerificationClass innerType;

  /**
   * Constructor for a VerificationListClass.
   *
   * @param name The name of the class
   * @param packageName The package name of the class
   * @param signature Optional signature of the class
   * @param constructors The constructors of the class
   * @param innerType The inner type of the list. e.g. verification class of "java.lang.String" for
   *     "List<String>"
   */
  public VerificationListClass(
      String name,
      String packageName,
      Optional<String> signature,
      List<ClassConstructor> constructors,
      VerificationClass innerType) {
    super(name, packageName, signature, constructors, false);
    this.innerType = innerType;
  }

  /** Returns the innerType of the list. */
  public VerificationClass getInnerType() {
    return innerType;
  }
}
