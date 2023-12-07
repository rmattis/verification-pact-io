package org.sosy_lab.jar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sosy_lab.jar.model.HttpMethod;
import org.sosy_lab.jar.model.SpringControllerClass;
import org.sosy_lab.jar.model.SpringEndpointMethod;
import org.sosy_lab.jar.model.SpringMethodParameter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JarFileLoaderTest {

  @Test
  public void parseJvmWorkshopProviderJarMustReturnCorrectDefinitions() {
    // Test an example jar file from pact jvm spring workshop
    // https://github.com/pact-foundation/pact-workshop-jvm-spring

    JarFileLoader jarFileLoader = new JarFileLoader();

    try {
      List<SpringControllerClass> controllerClasses =
          jarFileLoader.readSpringControllerClasses(
              new File(getClass().getClassLoader().getResource("provider.jar").getFile()));

      Assertions.assertEquals(1, controllerClasses.size());

      SpringControllerClass controllerClass = controllerClasses.get(0);
      Assertions.assertEquals(
          "au.com.dius.pactworkshop.provider.ProductController", controllerClass.name());
      Assertions.assertEquals(3, controllerClass.methods().size());

      Assertions.assertEquals(
          List.of(List.of("products"), List.of("product/{id}"), List.of("temperature/{year}")),
          controllerClass.methods().stream().map(SpringEndpointMethod::endpointPaths).toList());

      Assertions.assertEquals(
              List.of("getAllProducts", "getProductById", "getAverageTemperatureByYear"),
              controllerClass.methods().stream().map(SpringEndpointMethod::methodName).toList());

      Assertions.assertEquals(
          List.of(
              "java.util.List<au.com.dius.pactworkshop.provider.Product>",
              "org.springframework.http.ResponseEntity<au.com.dius.pactworkshop.provider.Product>",
              "au.com.dius" + ".pactworkshop.provider.YearTemperature"),
          controllerClass.methods().stream().map(SpringEndpointMethod::returnType).toList());

      Assertions.assertTrue(controllerClass.methods().stream()
              .allMatch(method -> method.httpMethod() == HttpMethod.GET));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
