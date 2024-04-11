package server;

import model.UserData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import com.google.gson.Gson;
import webSocketMessages.userCommands.UserGameCommand;

@WebSocket
public class WebsocketHandler {
  private static final Gson gson = new Gson();

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws Exception {
    // System.out.printf("Received: %s\n", message);
    // session.getRemote().sendString("WebSocket response: " + message);

    try {
      UserGameCommand userFromResponse = gson.fromJson(message, UserGameCommand.class);

      switch (userFromResponse.getCommandType()) {
        case JOIN_PLAYER:
        {
          System.out.println("JOIN_PLAYER");
        }
      }

    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
