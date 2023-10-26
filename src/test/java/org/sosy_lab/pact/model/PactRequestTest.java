package org.sosy_lab.pact.model;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.sosy_lab.util.DecodingTestHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PactRequestTest {

    @TestFactory
    public List<DynamicTest> makeSureAllPactV4TestcaseRequestsCanBeDecoded() {
        var helper = new DecodingTestHelper();
        var responseDirectory = "pact-v4-specification-testcases/request";
        var testDirectories = Stream.of("body", "headers", "method", "path", "query")
                .map(directory -> responseDirectory + "/" + directory).toList();

        return helper.testDecodingForFileDirectories(
                testDirectories,
                jsonNode -> Optional.of(jsonNode.required("expected")),
                PactRequest.class
        );
    }

}
