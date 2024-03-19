package dataAccessTests;

import dataAccess.GameDAO;
import dataAccess.GameException;
import dataAccess.SQLGameDAO;
import model.GameData;
import model.JoinGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameDAOTests {
  private GameDAO gameDAO;

  @BeforeEach
  public void setup() {
    gameDAO = new SQLGameDAO();
    gameDAO.clear();
  }

  @Test
  public void listGamesPositive() {
    ArrayList<GameData> games = gameDAO.listGames();

    Assertions.assertTrue(games.isEmpty(), "listGames should return an empty list when no games exist!");
  }

  @Test
  public void listGamesNegative() {
    gameDAO.createGame("NotEmptyGame");

    ArrayList<GameData> games = gameDAO.listGames();

    Assertions.assertFalse(games.isEmpty(), "listGames should return a non-empty list when games exist!");
  }

  @Test
  public void createGamePositive() {
    int id = gameDAO.createGame("TestGame");

    Assertions.assertNotEquals(0, id, "createGame should return a non-zero gameId!");
  }

  @Test
  public void createGameNegative() {
    int id1 = gameDAO.createGame("DuplicateGame");
    int id2 = gameDAO.createGame("DuplicateGame");

    Assertions.assertNotEquals(id1, id2, "createGame should allow games with duplicate names by generating unique game IDs!");
  }

  @Test
  public void joinGamePositive() throws GameException {
    String username = "testUser";
    int id = gameDAO.createGame("TestGameToJoin");

    JoinGameRequest joinRequest = new JoinGameRequest("WHITE", id);
    Assertions.assertDoesNotThrow(() -> gameDAO.joinGame(username, joinRequest), "joinGame should not throw an exception for a valid request!");
  }

  @Test
  public void joinGameNegative() {
    String username = "testUser";
    int id = gameDAO.createGame("TestGameUserJoined");

    JoinGameRequest joinRequest = new JoinGameRequest("WHITE", id);

    Assertions.assertDoesNotThrow(() -> gameDAO.joinGame(username, joinRequest), "Initial joinGame should succeed!");
    Assertions.assertThrows(GameException.class, () -> gameDAO.joinGame(username, joinRequest), "joinGame should throw GameException if the user tries to join the same game again!");
  }

  @Test
  public void clearPositive() {
    Assertions.assertDoesNotThrow(() -> gameDAO.clear(), "Clear method should not throw any exception!");
  }
}
