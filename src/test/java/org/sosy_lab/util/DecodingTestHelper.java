package org.sosy_lab.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Helper class that makes testing if decoding a given json file is successful easier. */
public class DecodingTestHelper {

  /**
   * Test that decoding for a specific class works with all files in a list of directories.
   *
   * @param fileDirectories A list of directories where we can find the files to test.
   * @param transformFunction A transform function where you get the jsonNode of the whole file and
   *     return just the node that should be decoded.
   * @param valueType The type of class you want to decode the jsonNode to.
   */
  public static <T> List<DynamicTest> testDecodingForFileDirectories(
      List<String> fileDirectories,
      Function<JsonNode, Optional<JsonNode>> transformFunction,
      Class<T> valueType) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());

    // Create a list with all .json files from all the provided directories
    List<File> testFiles =
        fileDirectories.stream()
            .flatMap(
                fileName -> {
                  File directory =
                      new File(
                          DecodingTestHelper.class
                              .getClassLoader()
                              .getResource(fileName)
                              .getFile());
                  return Arrays.stream(Objects.requireNonNull(directory.listFiles()));
                })
            .filter(file -> file.getName().endsWith(".json"))
            .toList();

    // Iterate over all files, apply the json transformation function and try to decode the result.
    return testFiles.stream()
        .map(
            file ->
                DynamicTest.dynamicTest(
                    "make sure file \"" + file.getName() + "\" can be decoded",
                    () -> {
                      try {
                        var jsonTree = mapper.readTree(file);

                        var jsonNode = transformFunction.apply(jsonTree);

                        if (jsonNode.isPresent()) {
                          mapper.treeToValue(jsonNode.get(), valueType);
                        }
                      } catch (IOException e) {
                        System.err.println("Test failed for file " + file.getAbsolutePath());
                        System.err.println(e);
                        Assertions.fail("Couldn't decode file " + file.getName());
                      }
                    }))
        .toList();
  }
}
