package org.sosy_lab.jar.model;

import java.util.List;

/**
 * Information about a spring endpoint method.
 *
 * @param methodName The name of the method (e.g. "getProduct")
 * @param httpMethod The http method used of this endpoint.
 * @param endpointPaths The paths of this endpoint (e.g. "/product/{id}")
 * @param parameters The parameters used in the function of this endpoint.
 * @param returnType The returnType of this endpoint (e.g.
 *     "java.util.List<au.com.dius.pactworkshop.provider.Product>")
 */
public record SpringEndpointMethod(
    String methodName,
    HttpMethod httpMethod,
    List<String> endpointPaths,
    List<SpringMethodParameter> parameters,
    String returnType) {}
