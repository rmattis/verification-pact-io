package org.sosy_lab.pact.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.sosy_lab.pact.model.PactSpecification;

import java.io.File;
import java.io.IOException;

/**
 * Parser for a pact file according to
 * <a href="https://github.com/pact-foundation/pact-specification/tree/version-4">the pact verification</a>
 */
public class PactFileParser {

    private final ObjectMapper objectMapper;

    public PactFileParser() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
    }

    /**
     * Tries reading a pact file and parsing it into a {@link PactSpecification}
     *
     * @param file The file that is tried to be parsed as pact specification.
     * @return The parsed pact specification
     * @throws IOException Exception when decoding fails
     */
    public PactSpecification parsePactFile(File file) throws IOException {
        return objectMapper.readValue(file, PactSpecification.class);
    }

}
