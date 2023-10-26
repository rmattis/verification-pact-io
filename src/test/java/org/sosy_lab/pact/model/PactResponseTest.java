package org.sosy_lab.pact.model;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.sosy_lab.util.DecodingTestHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PactResponseTest {

    @TestFactory
    public List<DynamicTest> makeSureAllPactV4TestcaseResponsesCanBeDecoded() {
        var responseDirectory = "pact-v4-specification-testcases/response";
        var testDirectories = Stream.of("body", "headers", "status").map(directory -> responseDirectory + "/" + directory).toList();

        return DecodingTestHelper.testDecodingForFileDirectories(
                testDirectories,
                jsonNode -> Optional.of(jsonNode.required("expected")),
                PactResponse.class
        );
    }

}
