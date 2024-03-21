import ui.UserInterface;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
  static UserInterface userInterface;
  static ServerFacade serverFacade;
  static boolean loggedIn = false;
  static boolean quit = false;

  public static void main(String[] args) {
    userInterface = UserInterface.getInstance();
    userInterface.init(new PrintStream(System.out, true, StandardCharsets.UTF_8));

    serverFacade = ServerFacade.getInstance();

    userInterface.printWelcomeHeader();

    while (loggedIn && postLogin() || preLogin()) {
      // ...
    }
  }

  public static boolean preLogin() {
    if (quit) {
      return false;
    }

    System.out.print("[LOGGED_OUT] >>> ");

    Scanner scanner = new Scanner(System.in);
    String line = scanner.nextLine();

    if (line.startsWith("register")) {
      String[] loginArguments = line.split(" ");

      if (loginArguments.length > 3) {
        String username = loginArguments[1];
        String password = loginArguments[2];
        String email = loginArguments[3];

        boolean registerSuccess = serverFacade.register(username, password, email);

        if (registerSuccess) {
          System.out.printf("Logged in as %s%n", username);

          loggedIn = true;
        } else {
          System.out.printf("There was an error registering%n");
        }
      } else {
        System.out.printf("Not enough arguments provided for register%n");
      }
    } else if (line.startsWith("login")) {
      String[] loginArguments = line.split(" ");

      if (loginArguments.length > 2) {
        String username = loginArguments[1];
        String password = loginArguments[2];

        boolean loginSuccess = serverFacade.login(username, password);

        if (loginSuccess) {
          System.out.printf("Logged in as %s%n", username);

          loggedIn = true;
        } else {
          System.out.printf("There was an error logging in%n");
        }
      } else {
        System.out.printf("Not enough arguments provided for login%n");
      }
    } else if (line.equals("help")) {
      userInterface.printHelpPreLogin();
    } else if (line.equals("quit")) {
      return false;
    }

    System.out.printf("%n");

    return true;
  }

  public static boolean postLogin() {
    System.out.print("[LOGGED_IN] >>> ");

    Scanner scanner = new Scanner(System.in);
    String line = scanner.nextLine();

    if (line.startsWith("create")) {
      String[] createArguments = line.split(" ");

      if (createArguments.length > 1) {
        String gameId = createArguments[1];

        boolean creationSuccess = serverFacade.create(gameId);

        if (creationSuccess) {
          System.out.printf("Created a new game%n");
        } else {
          System.out.printf("There was an error creating a game%n");
        }
      } else {
        System.out.printf("Not enough arguments provided for create%n");
      }
    } else if (line.startsWith("list")) {

    } else if (line.startsWith("join")) {
      //
    } else if (line.startsWith("observe")) {
      //
    } else if (line.startsWith("logout")) {
      return false;
    } else if (line.startsWith("help")) {
      userInterface.printHelpPostLogin();
    } else if (line.equals("quit")) {
      quit = true;

      return false;
    }

    System.out.printf("%n");

    return true;
  }
}