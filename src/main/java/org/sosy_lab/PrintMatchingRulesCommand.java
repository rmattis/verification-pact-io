package org.sosy_lab;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.sosy_lab.pact.model.PactMatchingRule;
import org.sosy_lab.pact.model.PactSpecification;
import org.sosy_lab.pact.model.matching.rule.PactMatchingRules;
import org.sosy_lab.pact.parser.PactFileParser;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * ** printMatchingRules Command **
 * <p>
 * The "printMatchingRules" command is capabable of reading a <a href="https://www.pact.io">Pact</a> file
 * following the
 * <a href="https://github.com/pact-foundation/pact-specification/tree/version-4#readme">Pact Specification (v4)</a>
 * and to print all matchingRules to the console.
 * </p>
 */
@CommandLine.Command(name = "printMatchingRules", mixinStandardHelpOptions = true)
public class PrintMatchingRulesCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "The pact file to read")
    private String pactFile;

    private final ObjectMapper objectMapper;
    private final PactFileParser pactFileParser;

    public PrintMatchingRulesCommand() {
        this.pactFileParser = new PactFileParser();

        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
    }

    @Override
    public Integer call() {
        File file = new File(pactFile);
        if (!file.exists()) {
            System.err.println("Couldn't find file " + pactFile);
            return 1;
        }

        try {
            PactSpecification specification = pactFileParser.parsePactFile(file);

            System.out.println("List of all matching rules:");
            specification.interactions().forEach(interaction -> {
                System.out.println("- " + interaction.description());
                interaction.request().ifPresent(request -> {
                    System.out.println("  - Request ");
                    printMatchingRules(request.matchingRules());
                });
                interaction.response().ifPresent(response -> {
                    System.out.println("  - Response ");
                    printMatchingRules(response.matchingRules());
                });
            });
        } catch (IOException e) {
            System.err.println("Failed to parse pact file " + pactFile + ": " + e.getMessage());
            return 1;
        }

        return 0;
    }

    /**
     * Helper method to print all matching rules for request or response
     */
    private void printMatchingRules(Optional<PactMatchingRules> matchingRulesOpt) {
        matchingRulesOpt.ifPresent(matchingRules -> {
            System.out.println("    - Body ");
            printMatchingRuleMap(matchingRules.body());
            System.out.println("    - Header ");
            printMatchingRuleMap(matchingRules.header());
            System.out.println("    - Metadata ");
            printMatchingRuleMap(matchingRules.metadata());
            System.out.println("    - Path ");
            if(matchingRules.path().isPresent()) {
                printSingleRule("path", matchingRules.path().get());
            } else {
                System.out.println("      No matching rules found ");
            }
            System.out.println("    - Query ");
            printMatchingRuleMap(matchingRules.query());
        });
    }

    /**
     * Helper method to print an optional matching rule map
     */
    private void printMatchingRuleMap(Optional<Map<String, PactMatchingRule>> matchingRule) {
        if (matchingRule.isPresent()) {
            matchingRule.get().forEach(this::printSingleRule);
        } else {
            System.out.println("      No matching rules found ");
        }
    }

    /**
     * Helper method to print a single rule
     */
    private void printSingleRule(String name, PactMatchingRule rule) {
        try {
            var jsonString = objectMapper.writeValueAsString(rule);
            System.out.println("      - " + name + ": " + jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
