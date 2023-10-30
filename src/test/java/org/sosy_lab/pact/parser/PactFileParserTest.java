package org.sosy_lab.pact.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class PactFileParserTest {

  @Test
  public void parseJVmWorkshopPactMustSucceed() {
    // Tries to parse the generated pact file copied from the pact-workshop-jvm-spring
    // https://github.com/pact-foundation/pact-workshop-jvm-spring
    PactFileParser pactFileParser = new PactFileParser();
    File file =
        new File(
            getClass()
                .getClassLoader()
                .getResource("pact-specifications/jvm_workshop_pact.json")
                .getFile());

    try {
      var specification = pactFileParser.parsePactFile(file);

      Assertions.assertEquals(specification.consumer().name(), "FrontendApplication");
      Assertions.assertEquals(specification.provider().name(), "ProductService");
      Assertions.assertEquals(specification.interactions().size(), 6);
    } catch (IOException e) {
      Assertions.fail("Failed to parse pact file " + file + ": " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
