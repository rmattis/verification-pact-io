package org.sosy_lab.generation.model;

import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The type of class based on the signature of the class used in a method or a constructor. This is
 * used to distinguish if we have to deal with a primitive type, a simple type, an array or an
 * object.
 */
public enum ClassType {
  STRING(JsonNodeType.STRING, "java.lang.String"),

  BOOLEAN(JsonNodeType.BOOLEAN, "java.lang.Boolean", "boolean"),

  BYTE(JsonNodeType.NUMBER, "java.lang.Byte", "byte"),

  SHORT(JsonNodeType.NUMBER, "java.lang.Short", "short"),

  INTEGER(JsonNodeType.NUMBER, "java.lang.Integer", "int"),

  LONG(JsonNodeType.NUMBER, "java.lang.Long", "long"),

  FLOAT(JsonNodeType.NUMBER, "java.lang.Float", "float"),

  DOUBLE(JsonNodeType.NUMBER, "java.lang.Double", "double"),

  OBJECT(JsonNodeType.OBJECT),

  ARRAY(
      JsonNodeType.ARRAY,
      List.of("java.util.List", "java.util.ArrayList", "java.util.Set"),
      List.of());

  private final JsonNodeType jsonNodeType;

  private final List<String> jvmTypeNames;
  private final List<String> primitiveNames;

  ClassType(JsonNodeType jsonNodeType) {
    this.jsonNodeType = jsonNodeType;
    this.jvmTypeNames = List.of();
    this.primitiveNames = List.of();
  }

  ClassType(JsonNodeType jsonNodeType, String jvmTypeName) {
    this.jsonNodeType = jsonNodeType;
    this.jvmTypeNames = List.of(jvmTypeName);
    primitiveNames = List.of();
  }

  ClassType(JsonNodeType jsonNodeType, String jvmTypeName, String primitiveName) {
    this.jsonNodeType = jsonNodeType;
    this.jvmTypeNames = List.of(jvmTypeName);
    this.primitiveNames = List.of(primitiveName);
  }

  ClassType(JsonNodeType jsonNodeType, List<String> jvmTypeNames, List<String> primitiveNames) {
    this.jsonNodeType = jsonNodeType;
    this.jvmTypeNames = jvmTypeNames;
    this.primitiveNames = primitiveNames;
  }

  /** Returns the json node type of this class type. */
  public JsonNodeType getJsonNodeType() {
    return jsonNodeType;
  }

  /**
   * Returns a list of jvmTypeNames (e.g. "java.lang.String").
   *
   * @return empty when this is not a simple type
   */
  public List<String> getJvmTypeNames() {
    return jvmTypeNames;
  }

  /**
   * Returns the primitive names (e.g. "double").
   *
   * @return empty when this is not a primitive type
   */
  public List<String> getPrimitiveNames() {
    return primitiveNames;
  }

  /**
   * Returns the classType from the generic signature of a class.
   *
   * @param signature The signature of a class used in a method or constructor
   */
  public static ClassType fromGenericSignature(String signature) {
    Optional<ClassType> simpleType =
        Arrays.stream(values())
            .filter(
                classType ->
                    classType.jvmTypeNames.contains(signature)
                        || classType.primitiveNames.contains(signature))
            .findFirst();

    if (simpleType.isEmpty()) {
      if (ClassType.ARRAY.jvmTypeNames.stream().anyMatch(signature::startsWith)) {
        return ClassType.ARRAY;
      } else {
        return ClassType.OBJECT;
      }
    } else {
      return simpleType.get();
    }
  }
}
