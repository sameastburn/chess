package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.CROWN;

public class UserInterface {
  private static final UserInterface instance = new UserInterface();
  private PrintStream out;

  public static UserInterface getInstance() {
    return instance;
  }

  public void init(PrintStream outputStream) {
    out = outputStream;
  }

  public void printPreLoginHeader() {
    out.printf(CROWN + "Welcome to 240 chess. Type Help to get started." + CROWN + "%n");
  }

  public void printHelp() {
    out.printf("\tregister <USERNAME> <PASSWORD> <EMAIL> - to create an account%n");
    out.printf("\tlogin <USERNAME> <PASSWORD> - to play chess%n");
    out.printf("\tquit - playing chess%n");
    out.printf("\thelp - with possible commands%n");
  }
}
