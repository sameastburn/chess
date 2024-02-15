package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.*;

import java.util.List;

public class GameService {
  private static final GameService instance = new GameService();
  private static final GameDAO gameDAO = new MemoryGameDAO();
  public static GameService getInstance() {
    return instance;
  }

  public void clear() {
    // ...
  }

  public List<GameData> listGames() {
    return gameDAO.listGames();
  }
}
