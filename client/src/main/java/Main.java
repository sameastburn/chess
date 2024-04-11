import chess.ChessGame;
import client.ServerFacade;
import ui.EscapeSequences;
import ui.UserInterface;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
  static UserInterface userInterface;
  static ServerFacade serverFacade;
  static boolean loggedIn = false;
  static boolean quit = false;
  static boolean inGame = false;
  static boolean redraw = false;

  public static void main(String[] args) {
    userInterface = UserInterface.getInstance();
    userInterface.init(new PrintStream(System.out, true, StandardCharsets.UTF_8));

    serverFacade = ServerFacade.getInstance();
    serverFacade.setPort(8080);

    if (!serverFacade.connect()) {
      System.out.print("There was an error connecting to the server!");

      return;
    }

    System.out.print(EscapeSequences.ERASE_SCREEN);

    userInterface.printWelcomeHeader();

    while (!quit) {
      if (loggedIn) {
        if (inGame) {
          game();
        } else {
          postLogin();
        }
      } else {
        preLogin();
      }
    }
  }

  public static void preLogin() {
    if (quit) {
      return;
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
          boolean registerLoginSuccess = serverFacade.login(username, password);

          if (registerLoginSuccess) {
            System.out.printf("Logged in as %s%n", username);

            loggedIn = true;
          } else {
            System.out.printf("There was an error logging in%n");
          }
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
      quit = true;

      return;
    }

    System.out.printf("%n");

    return;
  }

  public static void postLogin() {
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
      var games = serverFacade.list();

      if (games != null) {
        System.out.println("gameID | gameName | whiteUsername | blackUsername");

        for (var game : games) {
          System.out.println(game.gameID + " | " + game.gameName + " | " + game.whiteUsername + " | " + game.blackUsername);
        }
      } else {
        System.out.printf("There was an error listing the games%n");
      }
    } else if (line.startsWith("join")) {
      String[] joinArguments = line.split(" ");

      if (joinArguments.length > 2) {
        int gameId = Integer.parseInt(joinArguments[1]);
        String playerColor = joinArguments[2];
        ChessGame.TeamColor colorAsEnum = playerColor.toLowerCase() == "white" ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

        boolean joinSuccess = serverFacade.join(playerColor, gameId);

        if (joinSuccess) {
          serverFacade.joinGame(gameId, colorAsEnum);

          while (!serverFacade.receivedGame) {
            synchronized (serverFacade.receivedGameLock) {
              try {
                while (!serverFacade.receivedGame) {
                  serverFacade.receivedGameLock.wait();
                }
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                System.out.println(e);
              }
            }
          }

          System.out.print(EscapeSequences.ERASE_SCREEN);

          System.out.printf("Joined a game%n");

          inGame = true;
          // redraw = true;
        } else {
          System.out.printf("There was an error joining a game%n");
        }
      } else {
        System.out.printf("Not enough arguments provided for join%n");
      }
    } else if (line.startsWith("observe")) {
      String[] observeArguments = line.split(" ");

      if (observeArguments.length > 1) {
        int gameId = Integer.parseInt(observeArguments[1]);

        boolean observeSuccess = serverFacade.join(null, gameId);

        if (observeSuccess) {
          System.out.print(EscapeSequences.ERASE_SCREEN);

          System.out.printf("Joined a game as an observer%n");

          // userInterface.drawBothChessBoards();
        } else {
          System.out.printf("There was an error joining a game as an observer%n");
        }
      } else {
        System.out.printf("Not enough arguments provided for observe%n");
      }
    } else if (line.startsWith("logout")) {
      loggedIn = false;
    } else if (line.startsWith("help")) {
      userInterface.printHelpPostLogin();
    } else if (line.equals("quit")) {
      quit = true;

      return;
    }

    System.out.println();
  }

  public static void game() {
    if (redraw) {
      userInterface.drawBothChessBoards(userInterface.gameData);
      System.out.println();

      redraw = false;
    }

    System.out.print("[IN_GAME] >>> ");

    Scanner scanner = new Scanner(System.in);
    String line = scanner.nextLine();

    if (line.startsWith("help")) {
      userInterface.printHelpInGame();
    } else if (line.startsWith("redraw")) {
      redraw = true;
    } else if (line.startsWith("leave")) {
      serverFacade.leave();

      inGame = false;
    }

    System.out.println();
  }
}
