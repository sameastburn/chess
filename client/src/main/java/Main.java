import ui.UserInterface;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
  static UserInterface userInterface;

  public static void main(String[] args) {
    userInterface = UserInterface.getInstance();
    userInterface.init(new PrintStream(System.out, true, StandardCharsets.UTF_8));

    // boolean loggedIn = false;

    while (preLogin()) {
      // ...
    }
  }

  public static boolean preLogin() {
    System.out.printf("[LOGGED OUT] >>> ");

    Scanner scanner = new Scanner(System.in);
    String line = scanner.nextLine();

    if (line.equals("help")) {
      userInterface.printHelp();
    }


    if (line.equals("quit")) {
      return false;
    }

    System.out.printf("%n");

    return true;
  }
}