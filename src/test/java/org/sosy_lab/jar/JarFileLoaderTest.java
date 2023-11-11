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

      System.out.println(controllerClasses);

      Assertions.assertEquals(controllerClasses.size(), 1);

      SpringControllerClass controllerClass = controllerClasses.get(0);
      Assertions.assertEquals("au.com.dius.pactworkshop.provider.ProductController", controllerClass.name());
      Assertions.assertEquals(3, controllerClass.methods().size());

      SpringEndpointMethod getAllProductsMethod =
          new SpringEndpointMethod(
              "getAllProducts",
              HttpMethod.GET,
              List.of("products"),
              List.of(),
              "java.util.List<au.com.dius.pactworkshop.provider.Product>");

      SpringEndpointMethod getProductByIdEndpoint =
          new SpringEndpointMethod(
              "getProductById",
              HttpMethod.GET,
              List.of("product/{id}"),
              List.of(new SpringMethodParameter("id", "java.lang.String")),
              "org.springframework.http.ResponseEntity<au.com.dius.pactworkshop.provider.Product>");

      SpringEndpointMethod getAverageTemperatureByYearEndpoint =
              new SpringEndpointMethod(
                      "getAverageTemperatureByYear",
                      HttpMethod.GET,
                      List.of("temperature/{year}"),
                      List.of(new SpringMethodParameter("year", "int")),
                      "au.com.dius.pactworkshop.provider.YearTemperature");

      Assertions.assertEquals(
          controllerClass.methods(), List.of(getAllProductsMethod, getProductByIdEndpoint, getAverageTemperatureByYearEndpoint));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
