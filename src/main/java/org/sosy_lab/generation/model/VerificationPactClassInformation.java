package org.sosy_lab.generation.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.sosy_lab.generation.exception.MissingJsonNodeException;
import org.sosy_lab.generation.exception.NoMatchingConstructorException;
import org.sosy_lab.jar.model.ClassConstructor;
import org.sosy_lab.jar.model.VerificationClass;
import org.sosy_lab.pact.model.PactMatchingRule;

import java.util.*;

/**
 * Represents a class that was used in some way in a spring endpoint method.
 *
 * @param getterName The name of the getter in the parent class
 * @param verificationClass The verificationClass for the spring controller class
 * @param matchingRule The matching rule from the pact specification for this class.
 * @param jsonNode The jsonNode from the example based pact specification.
 * @param children The children of this class. This is only used for objects and arrays.
 */
public record VerificationPactClassInformation(
    String getterName,
    VerificationClass verificationClass,
    PactMatchingRule matchingRule,
    JsonNode jsonNode,
    List<VerificationPactClassInformation> children) {

  /**
   * Creates a VerificationPactClassInformation from a VerificationClass and a JsonNode.
   *
   * @param verificationClass The returnType verification class for a spring endpoint method.
   * @param jsonNode The jsonNode from the example based pact specification.
   * @param matchingRules The matching rules for this class from the pact specification.
   */
  public static VerificationPactClassInformation fromResponseClassAndJson(
      VerificationClass verificationClass,
      JsonNode jsonNode,
      Map<String, PactMatchingRule> matchingRules) {
    return fromClassAndJsonInternal(verificationClass, jsonNode, matchingRules, List.of());
  }

  /**
   * Internal method to create a VerificationPactClassInformation from a VerificationClass and a
   * JsonNode.
   *
   * @param verificationClass The returnType verification class for a spring endpoint method.
   * @param jsonNode The jsonNode from the example based pact specification.
   * @param matchingRules The matching rules for this class from the pact specification.
   * @param previousMatchingPaths The previous matching paths. This is used to create the full
   *     matching path for the current class.
   */
  private static VerificationPactClassInformation fromClassAndJsonInternal(
      VerificationClass verificationClass,
      JsonNode jsonNode,
      Map<String, PactMatchingRule> matchingRules,
      List<String> previousMatchingPaths) {
    List<Map.Entry<String, JsonNode>> fields = new ArrayList<>();
    jsonNode.fields().forEachRemaining(fields::add);

    Optional<ClassConstructor> bestMatchingConstructor =
        verificationClass.getConstructors().stream()
            .filter(constructor -> constructor.arguments().size() >= fields.size())
            .min(Comparator.comparingInt(constructor -> constructor.arguments().size()));

    if (bestMatchingConstructor.isEmpty()) {
      throw new NoMatchingConstructorException(
          "Couldn't find matching constructor for class " + verificationClass.getSimpleName());
    }
    ClassConstructor matchingConstructor = bestMatchingConstructor.get();

    List<VerificationPactClassInformation> children =
        matchingConstructor.arguments().stream()
            .map(
                argument -> {
                  String parameterName = argument.parameterName();
                  ClassType classType =
                      ClassType.fromGenericSignature(
                          argument.verificationClass().getSignature().get());

                  Optional<JsonNode> parameterNode =
                      Optional.ofNullable(jsonNode.get(parameterName));

                  if (parameterNode.isEmpty()) {
                    throw new MissingJsonNodeException(
                        "Couldn't find jsonNode for parameter "
                            + parameterName
                            + " in class "
                            + verificationClass.getSimpleName());
                  }

                  // TODO: Respect previous matching rules + be able to return multiple matching
                  // rules
                  PactMatchingRule matchingRule = matchingRules.get("$." + parameterName);

                  if (classType != ClassType.OBJECT && classType != ClassType.ARRAY) {
                    return new VerificationPactClassInformation(
                        argument.getterName().get(),
                        argument.verificationClass(),
                        matchingRule,
                        jsonNode,
                        List.of());
                  } else {
                    return fromClassAndJsonInternal(
                        argument.verificationClass(),
                        parameterNode.get(),
                        matchingRules,
                        previousMatchingPaths);
                  }
                })
            .toList();

    return new VerificationPactClassInformation("result", null, null, jsonNode, children);
  }
}
