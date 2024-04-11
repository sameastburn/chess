package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
  public Integer gameID;

  public ResignCommand(String authToken) {
    super(authToken);

    setCommandType(CommandType.RESIGN);
  }
}
