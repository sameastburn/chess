package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
  public Integer gameID;
  public ChessMove move;

  public MakeMoveCommand(String authToken) {
    super(authToken);

    setCommandType(CommandType.MAKE_MOVE);
  }
}
