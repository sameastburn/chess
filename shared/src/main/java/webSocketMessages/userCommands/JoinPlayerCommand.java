package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
  public Integer gameID;
  public ChessGame.TeamColor playerColor;

  public JoinPlayerCommand(String authToken) {
    super(authToken);

    setCommandType(CommandType.JOIN_PLAYER);
  }
}
