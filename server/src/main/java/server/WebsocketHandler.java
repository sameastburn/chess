package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccessExceptions.GameBadGameIDException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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

  public void sendNotification(String excludedUser, String message) throws IOException {
    NotificationMessage notificationMessage = new NotificationMessage();
    notificationMessage.message = message;

    connectionManager.broadcast(excludedUser, notificationMessage);
  }

  public void sendError(Session session, String message) throws IOException {
    ErrorMessage errorMessage = new ErrorMessage();
    errorMessage.errorMessage = "Error: " + message;

    session.getRemote().sendString(gson.toJson(errorMessage));
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws Exception {
    // TODO: delete this debug stuff when done
    // System.out.printf("Received: %s\n", message);
    // session.getRemote().sendString("WebSocket response: " + message);

    try {
      UserGameCommand gameCommand = gson.fromJson(message, UserGameCommand.class);

      switch (gameCommand.getCommandType()) {
        case JOIN_PLAYER: {
          JoinPlayerCommand joinCommand = gson.fromJson(message, JoinPlayerCommand.class);
          String authString = joinCommand.getAuthString();
          ChessGame.TeamColor playerColor = joinCommand.playerColor;

          // TODO: remove me, debug!
          System.out.println("JOIN_PLAYER");

          userService.authorize(authString);

          String username = userService.getUsernameFromToken(joinCommand.getAuthString());
          connectionManager.add(username, session);

          GameData gameNotNull = gameService.findGame(joinCommand.gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to join a nonexistent game"));

           if (playerColor == ChessGame.TeamColor.WHITE) {
            if (!gameNotNull.whiteUsername.equals(username)) {
              throw new RuntimeException("White spot already taken!");
            }
          } else if (playerColor == ChessGame.TeamColor.BLACK) {
            if (!gameNotNull.blackUsername.equals(username)) {
              throw new RuntimeException("Black spot already taken!");
            }
          }

          sendLoadGame(session, gameNotNull);
          sendNotification(username, username + "joined game '" + joinCommand.gameID + "' as '" + playerColor + "'");

          break;
        }
        case JOIN_OBSERVER: {
          // TODO: remove me, debug!
          System.out.println("JOIN_OBSERVER");

          JoinObserverCommand joinCommand = gson.fromJson(message, JoinObserverCommand.class);
          String authString = joinCommand.getAuthString();

          userService.authorize(authString);

          String username = userService.getUsernameFromToken(joinCommand.getAuthString());
          connectionManager.add(username, session);

          GameData gameNotNull = gameService.findGame(joinCommand.gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to join a nonexistent game"));

          sendLoadGame(session, gameNotNull);
          sendNotification(username, username + " is now observing game '" + joinCommand.gameID + "'");

          break;
        }
      }
    } catch (Exception e) {
      System.out.println(e);

      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));

      sendError(session, sw.toString());
    }
  }
}
