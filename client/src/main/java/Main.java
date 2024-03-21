import ui.UserInterface;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
  static UserInterface userInterface;
  static ClientAPI clientAPI;
  static boolean loggedIn = false;

  public static void main(String[] args) {
    userInterface = UserInterface.getInstance();
    userInterface.init(new PrintStream(System.out, true, StandardCharsets.UTF_8));

    clientAPI = ClientAPI.getInstance();

    userInterface.printWelcomeHeader();

    while (loggedIn && postLogin() || preLogin()) {
      // ...
    }
  }

  public static boolean preLogin() {
    System.out.print("[LOGGED_OUT] >>> ");

    Scanner scanner = new Scanner(System.in);
    String line = scanner.nextLine();

    if (line.startsWith("register")) {
      String[] loginArguments = line.split(" ");

      if (loginArguments.length > 2) {
        String username = loginArguments[1];
        String password = loginArguments[2];

        boolean registerSuccess = clientAPI.register(username, password);

        if (registerSuccess) {
          System.out.printf("Logged in as %s%n", username);

          loggedIn = true;
        }
      }
    } else if (line.startsWith("login")) {
      String[] loginArguments = line.split(" ");

      if (loginArguments.length > 2) {
        String username = loginArguments[1];
        String password = loginArguments[2];

        boolean loginSuccess = clientAPI.login(username, password);

        if (loginSuccess) {
          System.out.printf("Logged in as %s%n", username);

          loggedIn = true;
        }
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

    } else if (line.startsWith("list")) {

    } else if (line.startsWith("join")) {
      //
    } else if (line.startsWith("observe")) {
      //
    } else if (line.startsWith("logout")) {
      //
    } else if (line.startsWith("help")) {
      userInterface.printHelpPostLogin();
    } else if (line.equals("quit")) {
      return false;
    }

    System.out.printf("%n");

    return true;
  }
}