package org.sosy_lab;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class MainTest {

    private static final List<String> possibleCommands = List.of("-h", "--help", "--version",
            "printMatchingRules --help", "printMatchingRules -h",
            "printMatchingRules --version", "printMatchingRules test.json");
    private final Main main = new Main();
    private final CommandLine commandLine = new CommandLine(main);
    private final StringWriter stringWriter = new StringWriter();

    @BeforeEach
    public void beforeEach() {
        commandLine.setOut(new PrintWriter(stringWriter));
    }

    @Test
    public void makeSureExitCodeIsZeroForKnownCommands() {
        possibleCommands.forEach(command -> {
            int exitCode = commandLine.execute(command.split(" "));
            assert exitCode == 0;
        });
    }

    @Property
    public void makeSureCLIFailsForUnknownCommands(@ForAll @NotBlank String command) {
        int exitCode = commandLine.execute(command.split(" "));
        assert possibleCommands.contains(command) || exitCode == 2;
    }

}
