package dataAccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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

  public Optional<GameData> findGame(int gameID) {
    Optional<GameData> foundGame = games.stream().filter(game -> game.gameID == gameID).findFirst();

    return foundGame;
  }

  public int createGame(String gameName) {
    int newGameID = games.size() + 1;

    ChessGame newChessGame = new ChessGame();
    newChessGame.getBoard().resetBoard();

    GameData newGame = new GameData(newGameID, null, null, gameName, newChessGame);

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

  public void makeMove(int gameID, ChessMove move) throws GameException, InvalidMoveException {
    GameData gameNotNull = findGame(gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to make a move in a nonexistent game"));
    ChessGame chessGame = gameNotNull.game;

    chessGame.makeMove(move);
  }

  public void leaveGame(int gameID, String username) throws GameException {
    GameData game = findGame(gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to leave a nonexistent game"));

    if (!username.equals(game.whiteUsername) && !username.equals(game.blackUsername)) {
      throw new RuntimeException("Username is not part of this game");
    }

    if (username.equals(game.whiteUsername) && username.equals(game.blackUsername)) {
      throw new RuntimeException("User attempted to join a game with their previous zombie user");
    }

    if (username.equals(game.whiteUsername)) {
      game.whiteUsername = null;
    }

    if (username.equals(game.blackUsername)) {
      game.blackUsername = null;
    }
  }

  public void clear() {
    games.clear();
  }
}
