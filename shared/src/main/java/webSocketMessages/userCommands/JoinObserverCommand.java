package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand {
  public Integer gameID;

  public JoinObserverCommand(String authToken) {
    super(authToken);

    setCommandType(CommandType.JOIN_OBSERVER);
  }
}
