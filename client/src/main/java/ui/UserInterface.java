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

  public void printWelcomeHeader() {
    out.printf(CROWN + "Welcome to 240 chess. Type Help to get started." + CROWN + "%n");
  }

  public void printHelpPreLogin() {
    out.printf("\tregister <USERNAME> <PASSWORD> <EMAIL> - to create an account%n");
    out.printf("\tlogin <USERNAME> <PASSWORD> - to play chess%n");
    out.printf("\tquit - playing chess%n");
    out.printf("\thelp - with possible commands%n");
  }

  public void printHelpPostLogin() {
    out.printf("\tcreate <NAME> - a game%n");
    out.printf("\tlist - games%n");
    out.printf("\tjoin <ID> [WHITE|BLACK|<empty>] - a game%n");
    out.printf("\tobserve <ID> - a game%n");
    out.printf("\tlogout - when you are done%n");
    out.printf("\tquit - playing chess%n");
    out.printf("\thelp - with possible commands%n");
  }
}
