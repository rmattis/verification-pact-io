package org.sosy_lab.jar.model;

import java.util.List;

/**
 * Represents a class annotated with the spring @RestController annotation.
 *
 * @param name The name of the class (e.g. "au.com.dius.pactworkshop.provider.ProductController")
 * @param verificationClass The verificationClass for the spring controller class
 * @param methods All endpoint methods of the class
 */
public record SpringControllerClass(String name, VerificationClass verificationClass, List<SpringEndpointMethod> methods) {}
