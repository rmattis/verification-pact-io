package org.sosy_lab.pact.model;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.sosy_lab.util.DecodingTestHelper;

import java.util.List;
import java.util.Optional;

public class PactBodyTest {

  @TestFactory
  public List<DynamicTest> makeSureAllPactV4TestcaseBodiesCanBeDecoded() {
    var testDirectories =
        List.of(
            "pact-v4-specification-testcases/response/body",
            "pact-v4-specification-testcases/request/body");

    return DecodingTestHelper.testDecodingForFileDirectories(
        testDirectories,
        jsonNode ->
            jsonNode.has("body")
                ? Optional.of(jsonNode.required("expected").required("body"))
                : Optional.empty(),
        PactBody.class);
  }
}
