package dataAccess;

import model.GameData;

import java.util.List;

public class MemoryGameDAO implements GameDAO {
  List<GameData> games;

  @Override
  public List<GameData> listGames() {
    return games;
  }
}
