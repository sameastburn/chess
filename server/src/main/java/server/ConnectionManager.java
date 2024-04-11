package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
  public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
  private static final Gson gson = new Gson();

  public void add(String username, Session session, Integer gameID) {
    var connection = new Connection(username, session, gameID);
    connections.put(username, connection);
  }

  public void remove(String username) {
    connections.remove(username);
  }

  public void broadcast(String excludeUsername, int gameID, ServerMessage message) throws IOException {
    var removeList = new ArrayList<Connection>();
    for (var c : connections.values()) {
      if (c.gameID != null && c.gameID.equals(gameID)) {
        if (c.session.isOpen()) {
          if (!c.username.equals(excludeUsername)) {
            c.send(gson.toJson(message));
          }
        } else {
          removeList.add(c);
        }
      }
    }

    for (var c : removeList) {
      connections.remove(c.username);
    }
  }
}