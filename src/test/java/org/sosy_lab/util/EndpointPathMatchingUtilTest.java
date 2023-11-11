package org.sosy_lab.util;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sosy_lab.jar.model.HttpMethod;
import org.sosy_lab.jar.model.SpringEndpointMethod;
import org.sosy_lab.pact.model.PactInteraction;
import org.sosy_lab.pact.model.PactRequest;

import java.util.List;
import java.util.Optional;

public class EndpointPathMatchingUtilTest {

  @Test
  public void getPactInteractionsForMethodMustReturnOnlyInteractionsMatchingPathOfTheMethod() {
    SpringEndpointMethod endpointMethod =
        new SpringEndpointMethod(
            "test", HttpMethod.GET, List.of("temperature/{year}"), List.of(), "test");

    var matchingInteraction = pactInteractionFromPath(HttpMethod.GET, "temperature/2020");

    List<PactInteraction> pactInteractions =
        List.of(
            matchingInteraction,
            pactInteractionFromPath(HttpMethod.POST, "temperature/2020"),
            pactInteractionFromPath(HttpMethod.POST, "temperature"),
            pactInteractionFromPath(HttpMethod.POST, "temperature/2020/something"));

    var result =
        EndpointPathMatchingUtil.getPactInteractionsForMethod(endpointMethod, pactInteractions);

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(matchingInteraction, result.get(0));
  }

  private PactInteraction pactInteractionFromPath(HttpMethod method, String path) {
    return new PactInteraction(
        null, null, null, null, Optional.of(new PactRequest(method, path, null, Optional.empty())));
  }
}
