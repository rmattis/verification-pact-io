package org.sosy_lab.jar.model;

import java.util.List;

/**
 * Represents a constructor of a class that is (also recursively) used somewhere in a spring
 * controller class.
 *
 * @param arguments List of arguments defined in the constructor
 */
public record ClassConstructor(List<ClassConstructorArgument> arguments) {}
