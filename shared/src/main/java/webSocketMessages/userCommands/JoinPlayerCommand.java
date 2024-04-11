package webSocketMessages.userCommands;

import chess.ChessGame;

import static webSocketMessages.userCommands.UserGameCommand.CommandType.JOIN_PLAYER;

public class JoinPlayerCommand extends UserGameCommand {
  public JoinPlayerCommand(String authToken) {
    super(authToken);

    setCommandType(CommandType.JOIN_PLAYER);
  }

  public Integer gameID;
  public ChessGame.TeamColor playerColor;
}
