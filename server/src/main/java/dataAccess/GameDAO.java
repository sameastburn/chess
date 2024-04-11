package dataAccess;

import chess.ChessMove;
import chess.InvalidMoveException;
import model.GameData;
import model.JoinGameRequest;

import java.util.ArrayList;
import java.util.Optional;

public interface GameDAO {
  public ArrayList<GameData> listGames();
  public Optional<GameData> findGame(int gameID);
  public int createGame(String gameName);
  public void joinGame(String username, JoinGameRequest joinGameRequest) throws GameException;
  public void makeMove(int gameID, ChessMove move) throws GameException, InvalidMoveException;
  public void leaveGame(int gameID, String username) throws GameException;
  public void clear();
}
