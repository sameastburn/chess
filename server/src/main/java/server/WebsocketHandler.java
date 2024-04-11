package server;

import com.google.gson.Gson;
import dataAccessExceptions.GameBadGameIDException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
  private static final Gson gson = new Gson();
  private static final UserService userService = UserService.getInstance();
  private static final GameService gameService = GameService.getInstance();
  private final ConnectionManager connectionManager = new ConnectionManager();

  public void sendLoadGame(Session session, GameData game) throws IOException {
    LoadGameMessage loadGameMessage = new LoadGameMessage();
    loadGameMessage.game = game;

    session.getRemote().sendString(gson.toJson(loadGameMessage));
  }

  public void sendNotification(String selfUsername, String message) throws IOException {
    NotificationMessage notificationMessage = new NotificationMessage();
    notificationMessage.message = message;

    connectionManager.broadcast(selfUsername, notificationMessage);
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws Exception {
    // System.out.printf("Received: %s\n", message);
    // session.getRemote().sendString("WebSocket response: " + message);

    try {
      UserGameCommand gameCommand = gson.fromJson(message, UserGameCommand.class);

      switch (gameCommand.getCommandType()) {
        case JOIN_PLAYER: {
          JoinPlayerCommand joinCommand = gson.fromJson(message, JoinPlayerCommand.class);
          String authString = joinCommand.getAuthString();

          // TODO: remove me, debug!
          System.out.println("JOIN_PLAYER");

          userService.authorize(authString);

          String username = userService.getUsernameFromToken(joinCommand.getAuthString());
          connectionManager.add(username, session);

          GameData gameNotNull = gameService.findGame(joinCommand.gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to join a nonexistent game"));

          sendLoadGame(session, gameNotNull);
          sendNotification(username, username + " joined game: " + joinCommand.gameID);
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
