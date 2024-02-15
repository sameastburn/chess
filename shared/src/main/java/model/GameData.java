package model;

import chess.ChessGame;

// TODO: not sure why the spec wants this to be a record
// TODO: not really sure why we would want game data (very stateful) to be immutable
// TODO: maybe run by TAs?

public class GameData {
  public int gameID;
  public String whiteUsername;
  public String blackUsername;
  public String gameName;
  public ChessGame game;

  public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    this.gameID = gameID;
    this.whiteUsername = whiteUsername;
    this.blackUsername = blackUsername;
    this.gameName = gameName;
    this.game = game;
  }
}
