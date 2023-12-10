package org.sosy_lab.generation.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sosy_lab.jar.model.*;
import org.sosy_lab.pact.model.PactMatchingRule;
import org.sosy_lab.pact.model.matching.rule.rules.EqualityMatcher;
import org.sosy_lab.pact.model.matching.rule.rules.MinMaxMatcher;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VerificationPactClassInformationTest {

  @Test
  public void createClassFromResponseClassAndJson() {
    ObjectMapper objectMapper = new ObjectMapper();

    try {

      ObjectNode body = objectMapper.createObjectNode();
      body.put("year", 2023);
      body.put("temperature", 20);

      VerificationClass intClass =
          new VerificationClass("int", null, Optional.of("int"), List.of(), false);

      VerificationClass verificationClass =
          new VerificationClass(
              "YearTemperature",
              "au.com.dius.pactworkshop" + ".provider",
              Optional.of("au.com.dius.pactworkshop.provider.YearTemperature"),
              List.of(
                  new ClassConstructor(List.of()),
                  new ClassConstructor(
                      List.of(
                          new ClassConstructorArgument(
                              intClass, "temperature", Optional.of("getYear")),
                          new ClassConstructorArgument(
                              intClass, "temperature", Optional.of("getTemperature"))))),
              false);

      Map<String, PactMatchingRule> bodyMatchingRules =
          Map.of(
              "$.year",
              new PactMatchingRule(Optional.of("AND"), List.of(new EqualityMatcher())),
              "$.temperature",
              new PactMatchingRule(Optional.of("AND"), List.of(new MinMaxMatcher(0, 100))));

      var result =
          VerificationPactClassInformation.fromResponseClassAndJson(
              verificationClass, body, bodyMatchingRules);

      Assertions.assertEquals("result", result.getterName());
      Assertions.assertEquals(2, result.children().size());
      Assertions.assertEquals(body, result.jsonNode());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
