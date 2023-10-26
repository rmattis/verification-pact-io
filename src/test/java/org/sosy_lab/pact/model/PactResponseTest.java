package org.sosy_lab.pact.model;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.sosy_lab.util.DecodingTestHelper;

import java.util.List;
import java.util.Optional;

public class PactResponseTest {

    @TestFactory
    public List<DynamicTest> makeSureAllPactV4TestcaseResponsesCanBeDecoded() {
        var helper = new DecodingTestHelper();
        var responseDirectory = "pact-v4-specification-testcases/response";
        var testDirectories = List.of("body", "headers", "status").stream().map(directory -> responseDirectory + "/" + directory).toList();

        return helper.testDecodingForFileDirectories(
                testDirectories,
                jsonNode -> Optional.of(jsonNode.required("expected")),
                PactResponse.class
        );
    }

}
