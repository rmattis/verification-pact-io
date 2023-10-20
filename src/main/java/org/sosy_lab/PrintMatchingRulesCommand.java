package org.sosy_lab;

import picocli.CommandLine;

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

    @Override
    public Integer call() throws Exception {
        System.out.println("Received pactFile " + pactFile);
        return 0;
    }
}
