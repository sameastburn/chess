package dataAccess;

import chess.ChessGame;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;

import java.util.ArrayList;
import java.util.List;
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

  public void joinGame(String username, JoinGameRequest joinGameRequest) throws LoginUnauthorizedException {
    GameData gameNotNull = findGame(joinGameRequest.gameID()).orElseThrow(() -> new LoginUnauthorizedException("Game not found within database."));

    if (joinGameRequest.playerColor().equals("WHITE")) {
      gameNotNull.whiteUsername = username;
    } else {
      gameNotNull.blackUsername = username;
    }
  }
}
