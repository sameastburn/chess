package dataAccess;

import chess.ChessGame;
import dataAccessExceptions.GameBadGameIDException;
import dataAccessExceptions.GameColorTakenException;
import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;
import java.util.Optional;

public class MemoryGameDAO implements GameDAO {
  ArrayList<GameData> games = new ArrayList<>();

  @Override
  public ArrayList<GameData> listGames() {
    return games;
  }

  private Optional<GameData> findGame(int gameID) {
    Optional<GameData> foundGame = games.stream().filter(game -> game.gameID == gameID).findFirst();

    return foundGame;
  }

  public int createGame(String gameName) {
    int newGameID = games.size() + 1;
    GameData newGame = new GameData(newGameID, null, null, gameName, new ChessGame());

    games.add(newGame);

    return newGameID;
  }

  public void joinGame(String username, JoinGameRequest joinGameRequest) throws GameException {
    GameData gameNotNull = findGame(joinGameRequest.gameID()).orElseThrow(() -> new GameBadGameIDException("User attempted to join a nonexistent game"));

    if (joinGameRequest.playerColor() == null) {
      // observers
    } else if (joinGameRequest.playerColor().equals("WHITE")) {
      if (gameNotNull.whiteUsername != null) {
        throw new GameColorTakenException("User attempted to join a game with a taken color");
      }

      gameNotNull.whiteUsername = username;
    } else {
      if (gameNotNull.blackUsername != null) {
        throw new GameColorTakenException("User attempted to join a game with a taken color");
      }

      gameNotNull.blackUsername = username;
    }
  }

  public void clear() {
    games.clear();
  }
}
