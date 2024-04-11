package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand {
  public Integer gameID;

  public LeaveCommand(String authToken) {
    super(authToken);

    setCommandType(CommandType.LEAVE);
  }
}
