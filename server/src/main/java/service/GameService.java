package service;

import dataAccess.GameDAO;
import dataAccess.GameException;
import dataAccess.MemoryGameDAO;
import dataAccess.SQLGameDAO;
import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;

public class GameService {
  private static final GameService instance = new GameService();
  private static final GameDAO gameDAO = new SQLGameDAO();

  public static GameService getInstance() {
    return instance;
  }

  public void clear() {
    gameDAO.clear();
  }

  public ArrayList<GameData> listGames() {
    return gameDAO.listGames();
  }

  public int createGame(String gameName) {
    return gameDAO.createGame(gameName);
  }

  public void joinGame(String username, JoinGameRequest joinGameRequest) throws GameException {
    gameDAO.joinGame(username, joinGameRequest);
  }
}
