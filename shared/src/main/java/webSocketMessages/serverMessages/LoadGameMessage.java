package webSocketMessages.serverMessages;

import model.GameData;

import static webSocketMessages.serverMessages.ServerMessage.ServerMessageType.LOAD_GAME;

public class LoadGameMessage extends ServerMessage {
  public LoadGameMessage() {
    super(LOAD_GAME);
  }

  public GameData game;
}
