package org.sosy_lab;

import picocli.CommandLine;

/**
 * This class is the entrypoint of the CLI.
 * It defines all subcommands and their options.
 */
@CommandLine.Command(name = "app", mixinStandardHelpOptions = true, subcommands = {PrintMatchingRulesCommand.class})
public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
