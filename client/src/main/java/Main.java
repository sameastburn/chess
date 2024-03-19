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

    while (loggedIn && postLogin() || preLogin()) {
      // ...
    }
  }

  public static boolean preLogin() {
    System.out.printf("[LOGGED_OUT] >>> ");

    Scanner scanner = new Scanner(System.in);
    String line = scanner.nextLine();

    if (line.startsWith("login")) {
      String[] loginArguments = line.split(" ");

      if (loginArguments.length > 2) {
        String username = loginArguments[1];
        String password = loginArguments[2];

        boolean loginSuccess = clientAPI.login(username, password);

        if (loginSuccess) {
          System.out.printf("Login successful!%n");

          loggedIn = true;
        }
      }
    }
    if (line.equals("help")) {
      userInterface.printHelp();
    } else if (line.equals("quit")) {
      return false;
    }

    System.out.printf("%n");

    return true;
  }

  public static boolean postLogin() {
    System.out.printf("[LOGGED_IN] >>> ");

    Scanner scanner = new Scanner(System.in);
    String line = scanner.nextLine();

    return true;
  }
}