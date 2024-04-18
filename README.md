# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[Link to Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDAEQBKKA5kgM5hTACENBWqEULGMB4wOnMnXyFiwUiwAig4AEEQIFDx6TpMddswATLQCNpKGBeuZMnKBACu2GAGI0wKgCesly8-IJIwj4A7gAWSGAomIiopAC0AHzklDSwAFwwANoACgDyZAAqALowAPTuPJQAOmgA3vWUfgC2KAA0MLiGUdAWfSidwEgIAL6YojkwGcHyMPlQIXyUABTtUF29-dI8Q1AjMGMTCACUmHJkC5lmKzCcKGAAqg1Q259714+LjieZAAogAZYFwcowABUMAAYmwSgBZGA7GQAdQAEsC2MDUT9gN0YABeGAAfkw-0WtyeO00YGAxLQ7gQCBuXDui0e+RAa0EKA+Wx2ez6AyOw1G40mf209wc1ieAEkAHIgthQlXlEr4ygyb4dQn7MXHU7naUwABq2lBb2Bd02ZL6jvJ1xpXNlPL5CW07jAMX1u0NMrlgPyKrVGuVWqkvpiAH1IABrdB6nZxpCnJPoS7kMEQqEZp3wxEotEwLE4vHCw0k8mU2Wpakcp7AWPlCDJtDs5aNzJzSgttsd9CzbID3vwZDoZj5ABMAAZ580Wq2-e3OzN0BZnK4PF5vNBVC8YKCINw0NE4gkklO0n2x3lCqUKtUag1DBE0Mvq91RYcTTM-awE2yz5AgZ5IGgAYigcgzDK6zbuk8LzvJ80FBvWCwgXc+R0lo3acg8HowCAMQoCAiZFP+wwBvSwB-nBJzBgCCr5CC4KQrB4onMWyI6lAGLYri-F7LWFJUpkNL5MawxIoIpHEvw7iJG6RFPA0YA+muw5QT++xoCgUTrtmmEsUqqo4pG0arvGWZoKmnzppmOk5ux+YwIW5K8aWnyCZWIk1qS4kNthTwGUZOkEVh97UAO+ThcZXZAdFk4pDOMALkurQJTpm5oNuLhuJ4PiuCg6AnmeHjMN4sTxIkyTTnKyX5AU6h5uUwKvu+PCfsuNmJYBD5ylJMDgZwVWbP1LlRUh+RUMAyBWAkiWTUOnbMZkoa5hxUJ2d5MZ+gmOl+cJe1BaZoX5HZFoLRmilQMpM1qasYwQDQK1TetpmbaxpjtXiCJ8TZR2dideJnXWElLDhMA8O4egGDwM2LM1sPw-ohijrFwGLA1pBzouy7dIYwAvPkLCnpwLwWDAVUsHlBW7sV3jYO4UDYAg9hwF69gAOI1jVV71bezAo0NLXPlUtScIafVregfQy90yqGoN2Nyiw-PdHM4jGDIvMw7yKD8lrKCbErKAq901wGxrptmCUeswLz6hPEbJuGubhpWygNuu2ZYYWeqMCatqFt6hbPuK4aZAMgkOZWjadowA6Tp+yltv5BbirqJgtsTqj2e58lE54+lC4AIzLkXDM7kV+4BCgrIQFEMAAFIQJBzsC9YCCgImN5pU14tPm8Us1BbcvaZ2fRl3AEDgVA0fdDnavzIsmcwAAVp3aCm+hv7PIaOezyL8+LzbhHO67+Q75B+96X0LRF6faXn9AUzpwHMBvEUZgdUfYmuYoQAFIZBBXLEJPERcxIwG0MqV2YCQ53GVG8UEoI87+w3jDOGCNMb5zFurfIuCMZIxLrjEWTxMpE0RqTFA5MO6QRQDTU29NMBbkwEAA)

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared tests`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
