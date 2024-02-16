package dataAccess;

import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;

public interface GameDAO {
  public ArrayList<GameData> listGames();
  public int createGame(String gameName);
  public void joinGame(String username, JoinGameRequest joinGameRequest) throws LoginUnauthorizedException;
}
