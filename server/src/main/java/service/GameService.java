package service;

import dataAccess.*;
import model.*;

import java.util.ArrayList;

public class GameService {
  private static final GameService instance = new GameService();
  private static final GameDAO gameDAO = new MemoryGameDAO();
  public static GameService getInstance() {
    return instance;
  }

  public void clear() {
    // ...
  }

  public ArrayList<GameData> listGames() {
    return gameDAO.listGames();
  }

  public int createGame(String gameName) {
    return gameDAO.createGame(gameName);
  }

  public void joinGame(JoinGameRequest joinGameRequest) throws LoginUnauthorizedException {
    gameDAO.joinGame(joinGameRequest);
  }
}
