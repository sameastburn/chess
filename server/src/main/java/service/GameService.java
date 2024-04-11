package service;

import chess.ChessMove;
import chess.InvalidMoveException;
import dataAccess.GameDAO;
import dataAccess.GameException;
import dataAccess.SQLGameDAO;
import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;
import java.util.Optional;

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

  public Optional<GameData> findGame(int gameID) {
    return gameDAO.findGame(gameID);
  }

  public int createGame(String gameName) {
    return gameDAO.createGame(gameName);
  }

  public void joinGame(String username, JoinGameRequest joinGameRequest) throws GameException {
    gameDAO.joinGame(username, joinGameRequest);
  }

  public void makeMove(int gameID, ChessMove move) throws GameException, InvalidMoveException {
    gameDAO.makeMove(gameID, move);
  }
}
