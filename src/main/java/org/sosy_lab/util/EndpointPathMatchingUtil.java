package org.sosy_lab.util;

import org.sosy_lab.jar.model.SpringEndpointMethod;
import org.sosy_lab.pact.model.PactInteraction;

import java.util.List;
import java.util.regex.Pattern;

public class EndpointPathMatchingUtil {

  /**
   * Returns all pact interactions that match the path of the given endpoint.
   *
   * @param endpointMethod The endpoint method to match.
   * @param pactInteractions All pact interactions to check for a match.
   */
  public static List<PactInteraction> getPactInteractionsForMethod(
      SpringEndpointMethod endpointMethod, List<PactInteraction> pactInteractions) {
    return endpointMethod.endpointPaths().stream()
        .flatMap(
            path -> {
              // Regex to find all variables in the path with format "{variable}"
              // to replace with ".*" to match any string.
              String pathWithRegex = path.replaceAll("\\{.*?\\}", ".*");

              Pattern pathPattern = Pattern.compile(pathWithRegex);

              return pactInteractions.stream()
                  .filter(interaction -> interaction.request().isPresent())
                  // Check if the method of the endpoint is the same as method in the interaction
                  .filter(
                      interaction ->
                          interaction.request().get().method() == endpointMethod.httpMethod())
                  // Check the interaction path matches the definition of the endpoint
                  .filter(
                      interaction ->
                          pathPattern.matcher(interaction.request().get().path()).find());
            })
        .toList();
  }
}
