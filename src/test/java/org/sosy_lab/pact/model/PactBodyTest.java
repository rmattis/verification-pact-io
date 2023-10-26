package org.sosy_lab.pact.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.sosy_lab.util.DecodingTestHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PactBodyTest {

    @TestFactory
    public List<DynamicTest> newTest() {
        var helper = new DecodingTestHelper();
        var testDirectories = List.of("pact-v4-specification-testcases/response/body", "pact-v4-specification-testcases/request/body");

        return helper.testDecodingForFileDirectories(testDirectories,
                jsonNode -> jsonNode.has("body")
                        ? Optional.of(jsonNode.required("expected").required("body"))
                        : Optional.empty(),
                PactBody.class);
    }

    @TestFactory
    public List<DynamicTest> makeSureAllPactV4ResponseTestcaseBodiesCanBeDecoded() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        // Fetch all response body files from the pact-v4-specifications directory
        File directory = new File(getClass().getClassLoader().getResource("pact-v4-specification-testcases/response/body").getFile());
        var testFiles = Arrays.stream(Objects.requireNonNull(directory.listFiles())).toList();

        // Create a new test-case for every file and make sure the body can be decoded successfully.
        return testFiles.stream().map(file -> DynamicTest.dynamicTest("make sure file \"" + file.getName() + "\" can be decoded", () -> {
            try {
                var jsonTree = mapper.readTree(file);
                var expectedNode = jsonTree.required("expected");
                if (expectedNode.has("body")) {
                    var bodyNode = expectedNode.required("body");

                    mapper.treeToValue(bodyNode, PactBody.class);
                }
            } catch (IOException e) {
                Assertions.fail("Couldn't decode file " + file.getName());
                throw new RuntimeException(e);
            }
        })).toList();
    }

    @TestFactory
    public List<DynamicTest> makeSureAllPactV4RequestTestcaseBodiesCanBeDecoded() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());

        // Fetch all response body files from the pact-v4-specifications directory
        File directory =
                new File(getClass().getClassLoader().getResource("pact-v4-specification-testcases/request/body").getFile());
        var testFiles = Arrays.stream(Objects.requireNonNull(directory.listFiles())).toList();

        // Create a new test-case for every file and make sure the body can be decoded successfully.
        return testFiles.stream().map(file -> DynamicTest.dynamicTest("make sure file \"" + file.getName() + "\" can be decoded", () -> {
            try {
                var jsonTree = mapper.readTree(file);
                var expectedNode = jsonTree.required("expected");
                if (expectedNode.has("body")) {
                    var bodyNode = expectedNode.required("body");

                    mapper.treeToValue(bodyNode, PactBody.class);
                }
            } catch (IOException e) {
                Assertions.fail("Couldn't decode file " + file.getName());
                throw new RuntimeException(e);
            }
        })).toList();
    }

}
