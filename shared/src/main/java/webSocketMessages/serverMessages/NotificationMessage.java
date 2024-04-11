package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
  public NotificationMessage() {
    super(ServerMessageType.NOTIFICATION);
  }

  public String message;
}
