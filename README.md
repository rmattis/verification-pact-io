# Verification for Pact.Io

Goal of this project is to use formal verification to verify the correctness of a program given
a [Pact.io](https://www.pactio) specification file.

## Project Setup

Use [Maven](https://maven.apache.org/) to install the dependencies.

### Compilation

To compile the projects use ```mvn compile```

### Tests

To run the tests use ``mvn test``

## Run CLI

### Maven

To run the CLI directly in Maven, run the following commands (easiest to use during development):
```mvn -Dexec.args="printMatchingRules test.json"```
