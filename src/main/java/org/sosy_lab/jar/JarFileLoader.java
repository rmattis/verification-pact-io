package org.sosy_lab.jar;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.sosy_lab.jar.exception.ClassLoadFailedException;
import org.sosy_lab.jar.exception.UnknownSpringMappingAnnotationException;
import org.sosy_lab.jar.exception.WrongMethodSignatureException;
import org.sosy_lab.jar.model.HttpMethod;
import org.sosy_lab.jar.model.SpringControllerClass;
import org.sosy_lab.jar.model.SpringEndpointMethod;
import org.sosy_lab.jar.model.SpringMethodParameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** This class is used to read all spring controller classes from a jar file */
public class JarFileLoader {

  private static final String SPRING_ANNOTATION_NAME = "org.springframework.web.bind.annotation";

  private Map<String, HttpMethod> annotationToHttpMethod = new HashMap<>();

  public JarFileLoader() {
    annotationToHttpMethod =
        Map.of(
            springAnnotation("GetMapping"),
            HttpMethod.GET,
            springAnnotation("PostMapping"),
            HttpMethod.POST,
            springAnnotation("PutMapping"),
            HttpMethod.PUT,
            springAnnotation("DeleteMapping"),
            HttpMethod.DELETE);
  }

  private String springAnnotation(String annotationName) {
    return SPRING_ANNOTATION_NAME + "." + annotationName;
  }

  /**
   * This class reads all spring controller classes and their methods from a jar file.
   *
   * @param file The file for the jar file.
   * @return A list of all spring controller classes containg the most important functionality we
   *     need for the verification.
   */
  public List<SpringControllerClass> readSpringControllerClasses(File file) throws IOException {
    if (!file.exists()) {
      throw new FileNotFoundException("File " + file.getName() + " does not exist");
    }

    JarFile jarFile = new JarFile(file);

    // Read all classFiles included in the jar file.
    List<CtClass> classFiles =
        jarFile.stream()
            .filter(jarEntry -> jarEntry != null && jarEntry.getName().endsWith(".class"))
            .map(jarEntry -> jarMethodToJarFile(jarFile, jarEntry))
            .toList();

    return classFiles.stream()
        .filter(this::filterRestControllerClasses)
        .map(this::classToSpringController)
        .toList();
  }

  /**
   * Returns the classFile for a given jarEntry.
   *
   * @param jarFile The jarFile we read the jarEntry from.
   * @param jarEntry The jarEntry that is part of the jarFile.
   */
  private CtClass jarMethodToJarFile(JarFile jarFile, JarEntry jarEntry) {
    try {
      return ClassPool.getDefault().makeClass(jarFile.getInputStream(jarEntry));
    } catch (IOException e) {
      throw new ClassLoadFailedException(
          "Couldn't load jarFile class from jarEntry "
              + jarEntry.getName()
              + " from jarFile "
              + jarFile.getName(),
          e);
    }
  }

  /**
   * With this filter, we'll return all classes that are annotated with the spring @RestController
   *
   * @param ctClass The CtClass we want to check.
   * @return Whether the class as a @RestController annotation or not.
   */
  private boolean filterRestControllerClasses(CtClass ctClass) {
    AnnotationsAttribute annotationsAttribute =
        (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);

    Stream<Annotation> annotations =
        annotationsAttribute == null
            ? Stream.of()
            : Arrays.stream(annotationsAttribute.getAnnotations());

    return annotations.anyMatch(
        annotation -> annotation.getTypeName().equals(SPRING_ANNOTATION_NAME + ".RestController"));
  }

  /**
   * Returns the spring controller class for a given classFile.
   *
   * @param ctClass The CtClass for a class.
   */
  private SpringControllerClass classToSpringController(CtClass ctClass) {
    List<SpringEndpointMethod> endpointMethods =
        Arrays.stream(ctClass.getMethods())
            .filter(this::filterSpringEndpointMethods)
            .map(this::methodInfoToSpringEndpoint)
            .toList();

    return new SpringControllerClass(ctClass.getName(), ctClass, endpointMethods);
  }

  /**
   * Returns the spring endpoint method for a given methodInfo.
   *
   * @param ctMethod The information about the method.
   */
  private SpringEndpointMethod methodInfoToSpringEndpoint(CtMethod ctMethod) {
    MethodInfo methodInfo = ctMethod.getMethodInfo();
    MethodParametersAttribute methodParametersAttribute =
        (MethodParametersAttribute) methodInfo.getAttribute(MethodParametersAttribute.tag);

    AnnotationsAttribute annotationsAttribute =
        (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);

    Optional<Annotation> springMappingAnnotation =
        Stream.of(annotationsAttribute.getAnnotations())
            .filter(annotation -> annotationToHttpMethod.containsKey(annotation.getTypeName()))
            .findFirst();

    final AtomicReference<List<String>> endpointPath = new AtomicReference<>();
    final AtomicReference<HttpMethod> httpMethod = new AtomicReference<>();
    springMappingAnnotation.ifPresentOrElse(
        annotation -> {
          httpMethod.set(annotationToHttpMethod.get(annotation.getTypeName()));

          ArrayMemberValue valueArray =
              annotation.getMemberValue("value") != null
                  ? (ArrayMemberValue) annotation.getMemberValue("value")
                  : (ArrayMemberValue) annotation.getMemberValue("path");

          List<String> endpointPaths =
              Arrays.stream(valueArray.getValue())
                  .map(memberValue -> ((StringMemberValue) memberValue).getValue())
                  .toList();
          endpointPath.set(endpointPaths);
        },
        () -> {
          throw new UnknownSpringMappingAnnotationException(
              "Failed to find spring annotation for method " + methodInfo.getName());
        });

    try {
      SignatureAttribute.MethodSignature methodSignature =
          SignatureAttribute.toMethodSignature(
              ctMethod.getGenericSignature() != null
                  ? ctMethod.getGenericSignature()
                  : ctMethod.getSignature());

      List<SpringMethodParameter> methodParameters =
          methodParametersAttribute == null
              ? List.of()
              : IntStream.range(0, methodParametersAttribute.size())
                  .mapToObj(
                      index -> {
                        String name = methodParametersAttribute.parameterName(index);
                        SignatureAttribute.Type type = methodSignature.getParameterTypes()[index];

                        return new SpringMethodParameter(name, type.jvmTypeName());
                      })
                  .toList();

      return new SpringEndpointMethod(
          methodInfo.getName(),
          httpMethod.get(),
          endpointPath.get(),
          methodParameters,
          methodSignature.getReturnType().jvmTypeName());
    } catch (BadBytecode e) {
      throw new WrongMethodSignatureException(
          "Failed to read methodSignature from method's signature", e);
    }
  }

  /**
   * Filter all methods that are exposed as spring endpoint. For instance a method with
   * an @GetMapping annotation will be returned then. A method without annotation won't be returned
   * in this filter.
   *
   * @param ctMethod The ctMethod for the method
   * @return Whether this is a method for a spring endpoint or not.
   */
  private boolean filterSpringEndpointMethods(CtMethod ctMethod) {
    AnnotationsAttribute annotationsAttribute =
        (AnnotationsAttribute)
            ctMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);

    return annotationsAttribute != null
        && Stream.of(annotationsAttribute.getAnnotations())
            .anyMatch(annotation -> annotationToHttpMethod.containsKey(annotation.getTypeName()));
  }
}
