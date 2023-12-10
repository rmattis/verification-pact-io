package org.sosy_lab.jar.model;

import java.util.Optional;

/**
 * Represents a constructor of a class that is (also recursively) used somewhere in a spring
 * controller class.
 *
 * @param verificationClass The type of the class of this argument
 * @param parameterName The name of the parameter in the constructor
 * @param getterName Optional name of the getter method of the parameter in the class where the
 *     constructor is defined. NOTE: The name of the getter is not part of the constructor, but of
 *     the class itself, but we need this information anyway and pass it here.
 */
public record ClassConstructorArgument(
    VerificationClass verificationClass, String parameterName, Optional<String> getterName) {}
