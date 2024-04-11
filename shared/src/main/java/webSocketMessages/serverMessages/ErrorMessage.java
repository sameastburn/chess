package webSocketMessages.serverMessages;

import static webSocketMessages.serverMessages.ServerMessage.ServerMessageType.ERROR;

public class ErrorMessage extends ServerMessage {
  public ErrorMessage() {
    super(ERROR);
  }

  public String errorMessage;
}
