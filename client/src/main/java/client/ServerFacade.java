package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import exception.ResponseException;
import model.*;
import ui.UserInterface;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class ServerFacade extends Endpoint {
  private static final ServerFacade instance = new ServerFacade();
  private static final UserInterface userInterface = new UserInterface();
  private static final Gson gson = new Gson();
  private String authToken = "";
  private Integer port = 0;
  private Session session;
  private Integer gameID = -1;
  public volatile boolean receivedGame = false;
  public final Object receivedGameLock = new Object();

  public static ServerFacade getInstance() {
    return instance;
  }

  private static void writeBody(Object request, HttpURLConnection http) throws IOException {
    if (request != null) {
      http.addRequestProperty("Content-Type", "application/json");
      String reqData = new Gson().toJson(request);
      try (OutputStream reqBody = http.getOutputStream()) {
        reqBody.write(reqData.getBytes());
      }
    }
  }

  private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
    T response = null;
    if (http.getContentLength() < 0) {
      try (InputStream respBody = http.getInputStream()) {
        InputStreamReader reader = new InputStreamReader(respBody);
        if (responseClass != null) {
          response = new Gson().fromJson(reader, responseClass);
        }
      }
    }
    return response;
  }

  public void onMessageFacade(String message) {
    try {
      ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

      switch (serverMessage.getServerMessageType()) {
        case LOAD_GAME: {
          LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
          GameData gameData = loadGameMessage.game;

          this.gameID = gameData.gameID;

          userInterface.setGameData(gameData);

          synchronized (receivedGameLock) {
            this.receivedGame = true;

            receivedGameLock.notifyAll();
          }

          break;
        }
        case ERROR: {
          ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);

          System.out.println(errorMessage.errorMessage);

          break;
        }
        case NOTIFICATION: {
          NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);

          System.out.println(notificationMessage.message);

          break;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public boolean connect() {
    try {
      URI uri = new URI("ws://localhost:8080/connect");
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session = container.connectToServer(this, uri);

      //MessageHandler is not a functional interface :(
      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        public void onMessage(String message) {
          onMessageFacade(message);
        }
      });

      return true;
    } catch (Exception e) {
      System.out.println(e);

      return false;
    }
  }

  public boolean send(UserGameCommand command) {
    try {
      this.session.getBasicRemote().sendText(gson.toJson(command));

      return true;
    } catch (Exception e) {
      System.out.println(e);

      return false;
    }
  }

  public void joinGame(Integer gameID, ChessGame.TeamColor playerColor) {
    JoinPlayerCommand joinCommand = new JoinPlayerCommand(authToken);
    joinCommand.gameID = gameID;
    joinCommand.playerColor = playerColor;

    send(joinCommand);
  }

  public void leave() {
    LeaveCommand leaveCommand = new LeaveCommand(authToken);
    leaveCommand.gameID = gameID;

    send(leaveCommand);
  }

  public void resign() {
    ResignCommand resignCommand = new ResignCommand(authToken);
    resignCommand.gameID = gameID;

    send(resignCommand);
  }

  public void move(ChessMove newMove) {
    MakeMoveCommand moveCommand = new MakeMoveCommand(authToken);
    moveCommand.gameID = gameID;
    moveCommand.move = newMove;

    send(moveCommand);
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public boolean login(String username, String password) {
    try {
      var loginRequest = new LoginRequest(username, password);
      var response = this.makeRequest("POST", "/session", loginRequest, LoginResult.class);

      authToken = response.authToken();

      return true;
    } catch (Exception e) {
      System.out.println(e);

      return false;
    }
  }

  public boolean register(String username, String password, String email) {
    try {
      var registerRequest = new UserData(username, password, email);
      this.makeRequest("POST", "/user", registerRequest, UserData.class);

      return true;
    } catch (Exception e) {
      System.out.println(e);

      return false;
    }
  }

  public boolean create(String gameId) {
    try {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("gameName", gameId);

      this.makeRequest("POST", "/game", jsonObject, JsonObject.class);

      return true;
    } catch (Exception e) {
      System.out.println(e);

      return false;
    }
  }

  public ArrayList<GameData> list() {
    try {
      JsonObject response = this.makeRequest("GET", "/game", null, JsonObject.class);

      if (response.has("games") && response.get("games").isJsonArray()) {
        JsonArray gamesJsonArray = response.getAsJsonArray("games");
        var gameListType = new TypeToken<ArrayList<GameData>>() {
        }.getType();

        Gson gson = new Gson();
        ArrayList<GameData> gamesList = gson.fromJson(gamesJsonArray, gameListType);

        return gamesList;
      } else {
        System.out.println("The games field is missing or not an array");

        return null;
      }
    } catch (Exception e) {
      System.out.println(e);

      return null;
    }
  }


  public boolean join(String playerColor, int gameId) {
    try {
      var joinRequest = new JoinGameRequest(playerColor, gameId);
      this.makeRequest("PUT", "/game", joinRequest, JsonObject.class);

      return true;
    } catch (Exception e) {
      System.out.println(e);

      return false;
    }
  }

  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
    try {
      URL url = (new URI("http://localhost:" + port + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);
      http.setDoOutput(true);

      if (!authToken.isEmpty()) {
        http.addRequestProperty("Authorization", authToken);
      }

      writeBody(request, http);
      http.connect();
      throwIfNotSuccessful(http);
      return readBody(http, responseClass);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
    var status = http.getResponseCode();
    if (!isSuccessful(status)) {
      throw new ResponseException(status, "failure: " + status);
    }
  }

  public boolean isSuccessful(int status) {
    return status / 100 == 2;
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {

  }
}
