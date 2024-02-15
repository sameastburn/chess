package dataAccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
  public List<GameData> listGames();
}
