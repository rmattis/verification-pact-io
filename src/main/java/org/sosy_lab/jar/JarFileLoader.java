package org.sosy_lab.jar;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.sosy_lab.jar.exception.ClassLoadFailedException;
import org.sosy_lab.jar.exception.ClassNotFoundInClassPathException;
import org.sosy_lab.jar.exception.UnknownSpringMappingAnnotationException;
import org.sosy_lab.jar.exception.WrongMethodSignatureException;
import org.sosy_lab.jar.model.*;

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
   * Returns the {@see VerificationClass} for a given {@see CtClass}.
   *
   * @param ctClass The ctClass we want to map to a VerificationClass
   */
  private VerificationClass verificationClassFromCtClass(CtClass ctClass) {
    List<ClassConstructor> constructors =
        Arrays.stream(ctClass.getConstructors())
            .map(
                ctConstructor -> {
                  try {
                    List<CtClass> parameterTypes = Arrays.asList(ctConstructor.getParameterTypes());
                    MethodInfo methodInfo = ctConstructor.getMethodInfo();
                    MethodParametersAttribute methodParametersAttribute =
                        (MethodParametersAttribute)
                            methodInfo.getAttribute(MethodParametersAttribute.tag);

                    SignatureAttribute.MethodSignature methodSignature =
                        SignatureAttribute.toMethodSignature(
                            ctConstructor.getGenericSignature() != null
                                ? ctConstructor.getGenericSignature()
                                : ctConstructor.getSignature());

                    List<ClassConstructorArgument> constructorArguments = new ArrayList<>();
                    if (methodParametersAttribute != null) {
                      for (int i = 0; i < methodParametersAttribute.size(); i++) {
                        String parameterName = methodParametersAttribute.parameterName(i);
                        CtClass parameterType = parameterTypes.get(i);
                        String signature = methodSignature.getParameterTypes()[i].jvmTypeName();

                        constructorArguments.add(
                            new ClassConstructorArgument(
                                verificationClassFromClassAndSignature(parameterType, signature),
                                parameterName,
                                findGetterMethod(ctClass, parameterType, parameterName)));
                      }
                    }

                    return new ClassConstructor(constructorArguments);
                  } catch (NotFoundException e) {
                    // We intentionally return null here to enable partial parsing of an endpoint
                    return null;
                  } catch (BadBytecode e) {
                    throw new WrongMethodSignatureException(
                        "Failed to read constructor method signature in class " + ctClass.getName(),
                        e);
                  }
                })
            .toList();

    return new VerificationClass(
        ctClass.getSimpleName(),
        ctClass.getPackageName(),
        Optional.empty(),
        constructors,
        ctClass.isInterface());
  }

  /**
   * Method to find a getter method for a given parameter inside a class.
   *
   * @param ctClass The class where we expect a getter.
   * @param parameterType The type of the parameter used in the constructor.
   * @param parameterName The name of the parameter used in the constructor.
   */
  private Optional<String> findGetterMethod(
      CtClass ctClass, CtClass parameterType, String parameterName) {
    return Arrays.stream(ctClass.getMethods())
        .filter(
            method -> {
              try {
                return method.getReturnType().equals(parameterType)
                    && (method.getName().equalsIgnoreCase(parameterName)
                        || method.getName().equalsIgnoreCase("get" + parameterName)
                        || method.getName().equalsIgnoreCase("is" + parameterName));
              } catch (NotFoundException e) {
                // We intentionally return false here to enable partial parsing of an endpoint
                return false;
              }
            })
        .findFirst()
        .map(CtMethod::getName);
  }

  /**
   * Returns the {@see VerificationClass} for a given {@see CtClass} and signature.
   *
   * @param ctClass The ctClass we want to map to a VerificationClass
   * @param signature The signature of the class
   */
  private VerificationClass verificationClassFromClassAndSignature(
      CtClass ctClass, String signature) {
    // TODO: Add more cases where we have an array (e.g. ArrayList, LinkedList, Set, etc...)
    if (signature.contains("java.util.List")) {
      String className = signature.replace("java.util.List<", "").replace(">", "");
      VerificationClass listClass = verificationClassFromCtClass(ctClass);
      try {
        VerificationClass innerClass =
            verificationClassFromCtClass(ClassPool.getDefault().get(className));

        return new VerificationListClass(
            listClass.getSimpleName(),
            listClass.getPackageName(),
            Optional.of(signature),
            listClass.getConstructors(),
            innerClass);
      } catch (NotFoundException ex) {
        throw new ClassNotFoundInClassPathException(
            "Couldn't find class " + className + " in the classpath", ex);
      }
    } else {
      VerificationClass createdClass = verificationClassFromCtClass(ctClass);
      return new VerificationClass(
          createdClass.getSimpleName(),
          createdClass.getPackageName(),
          Optional.of(signature),
          createdClass.getConstructors(),
          createdClass.isInterface());
    }
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

    return new SpringControllerClass(
        ctClass.getName(), verificationClassFromCtClass(ctClass), endpointMethods);
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
          methodSignature.getReturnType().jvmTypeName(),
          verificationClassFromClassAndSignature(
              ctMethod.getReturnType(), methodSignature.getReturnType().jvmTypeName()));
    } catch (BadBytecode e) {
      throw new WrongMethodSignatureException(
          "Failed to read methodSignature from method's signature", e);
    } catch (NotFoundException e) {
      // We intentionally return null here to enable partial parsing of an endpoint
      return null;
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
