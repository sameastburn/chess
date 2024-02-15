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

  public void joinGame(JoinGameRequest joinGameRequest) throws LoginUnauthorizedException {
    // TODO: need new exception for this
    GameData gameNotNull = findGame(joinGameRequest.gameID()).orElseThrow(() -> new LoginUnauthorizedException("Game not found within database."));

    // TODO: !!!!!!!!! pickup from here!
    // TODO: NEED to rewrite whole tokens to be a map with token : username, as predicted
    // TODO: then, pass in username to joinGameRequest, set whiteUsername/blackUsername to username

    if (joinGameRequest.playerColor().equals("WHITE")) {
      gameNotNull.whiteUsername = joinGameRequest.playerColor();
    } else {
      gameNotNull.blackUsername = joinGameRequest.playerColor();
    }
  }
}
