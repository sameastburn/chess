package passoffTests.serverTests;

import dataAccess.GameException;
import model.JoinGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import passoffTests.testClasses.TestException;
import service.GameService;

public class GameServiceTests {

  @BeforeEach
  public void setup() {
    GameService.getInstance().clear();
  }

  @Test
  public void getInstancePositive()  {
    GameService instance1 = GameService.getInstance();
    GameService instance2 = GameService.getInstance();

    assertSame(instance1, instance2, "Multiple calls to getInstance() did not return the same instance");
  }

  @Test
  public void getInstanceNegative() {
    GameService gameService = GameService.getInstance();

    Assertions.assertNotNull(gameService, "getInstance() returned null");
  }

  @Test
  public void clearPositive() {
    GameService gameService = GameService.getInstance();

    gameService.createGame("new-game");
    gameService.clear();

    Assertions.assertTrue(gameService.listGames().size() == 0, "Clear didn't clear all games");
  }

  @Test
  public void clearNegative() {
    GameService gameService = GameService.getInstance();

    gameService.createGame("new-game");

    gameService.clear();

    JoinGameRequest joinRequest = new JoinGameRequest("WHITE", 1);
    assertThrows(GameException.class, () -> gameService.joinGame("bad-username", joinRequest),"Clear didn't clear all games");
  }

  @Test
  public void listGamesPositive() {
    GameService gameService = GameService.getInstance();
    gameService.createGame("new-game-1");

    Assertions.assertTrue(gameService.listGames().size() == 1, "List games size wasn't 1 after creating a game");
  }

  @Test
  public void listGamesNegative() {
    GameService gameService = GameService.getInstance();

    Assertions.assertNotNull(gameService.listGames(), "List games was null");
  }

  @Test
  public void createGamePositive() {
    GameService gameService = GameService.getInstance();

    int newGameID = gameService.createGame("new-game-1");

    Assertions.assertTrue(gameService.listGames().get(newGameID - 1).gameID == newGameID, "Game wasn't found after creating a new game");
  }

  @Test
  public void createGameNegative() {
    GameService gameService = GameService.getInstance();

    int newGameID = gameService.createGame("new-game-1");

    Assertions.assertFalse(newGameID <= 0, "Game ID wasn't positive");
  }

  @Test
  public void joinGamePositive() throws GameException {
    GameService gameService = GameService.getInstance();

    int newGameID = gameService.createGame("new-game-1");

    JoinGameRequest joinRequest = new JoinGameRequest("WHITE", newGameID);
    gameService.joinGame("fake-username", joinRequest);

    Assertions.assertEquals(gameService.listGames().get(newGameID - 1).whiteUsername,"fake-username", "White username didn't get replaced");
  }

  @Test
  public void joinGameNegative() throws GameException {
    GameService gameService = GameService.getInstance();

    int newGameID = gameService.createGame("new-game-1");

    JoinGameRequest joinRequest = new JoinGameRequest("WHITE", newGameID);

    gameService.joinGame("fake-username", joinRequest);

    assertThrows(GameException.class, () -> gameService.joinGame("fake-username-2", joinRequest),"joinGame didn't throw with conflicting user trying to join game");
  }

}
