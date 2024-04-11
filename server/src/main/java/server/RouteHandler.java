package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataAccess.*;
import dataAccessExceptions.*;
import model.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class RouteHandler {
  private static final RouteHandler instance = new RouteHandler();
  private static final Gson gson = new Gson();
  private static final UserService userService = UserService.getInstance();
  private static final GameService gameService = GameService.getInstance();
  private static final WebsocketHandler webSocketHandler = WebsocketHandler.getInstance();

  public static RouteHandler getInstance() {
    return instance;
  }

  public Object db(Request request, Response response) {
    userService.clear();
    gameService.clear();
    webSocketHandler.clearResignedGames();

    return new JsonObject();
  }

  public Object user(Request request, Response response) throws RegisterException {
    UserData userFromResponse = gson.fromJson(request.body(), UserData.class);

    try {
      return userService.register(userFromResponse);
    } catch (RegisterBadRequestException e) {
      response.status(400);

      return new FailureResponse("Error: bad request");
    } catch (RegisterAlreadyTakenException e) {
      response.status(403);

      return new FailureResponse("Error: already taken");
    }
  }

  public Object session(Request request, Response response) {
    LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

    try {
      return userService.login(loginRequest);
    } catch (LoginUnauthorizedException e) {
      response.status(401);

      return new FailureResponse("Error: unauthorized");
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }

  public Object gameGet(Request request, Response response) {
    String authToken = request.headers("Authorization");

    try {
      userService.authorize(authToken);

      ArrayList<GameData> games = gameService.listGames();

      JsonArray gamesArray = new JsonArray();
      for (GameData game : games) {
        String jsonString = gson.toJson(game);
        JsonObject gameJson = gson.fromJson(jsonString, JsonObject.class);

        gamesArray.add(gameJson);
      }

      JsonObject jsonObject = new JsonObject();
      jsonObject.add("games", gamesArray);

      return jsonObject;
    } catch (LoginUnauthorizedException e) {
      response.status(401);

      return new FailureResponse("Error: unauthorized");
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }

  public Object gamePost(Request request, Response response) {
    try {
      String authToken = request.headers("Authorization");

      JsonObject body = gson.fromJson(request.body(), JsonObject.class);
      JsonElement gameNameAsElement = body.get("gameName");

      if (gameNameAsElement == null) {
        response.status(400);

        return new FailureResponse("Error: bad request");
      }

      String gameName = gameNameAsElement.getAsString();

      userService.authorize(authToken);
      int newGameID = gameService.createGame(gameName);

      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("gameID", newGameID);

      return jsonObject;
    } catch (LoginUnauthorizedException e) {
      response.status(401);

      return new FailureResponse("Error: unauthorized");
    }
  }

  public Object sessionDelete(Request request, Response response) throws Exception {
    try {
      String authToken = request.headers("Authorization");
      userService.logout(authToken);

      return new JsonObject();
    } catch (LoginUnauthorizedException e) {
      response.status(401);

      return new FailureResponse("Error: unauthorized");
    } catch (LoginException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Object gamePut(Request request, Response response) throws Exception {
    try {
      String authToken = request.headers("Authorization");
      userService.authorize(authToken);

      JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
      String username = userService.getUsernameFromToken(authToken);

      gameService.joinGame(username, joinGameRequest);

      return new JsonObject();
    } catch (LoginUnauthorizedException e) {
      response.status(401);

      return new FailureResponse("Error: unauthorized");
    } catch (GameBadGameIDException e) {
      response.status(400);

      return new FailureResponse("Error: bad request");
    } catch (GameColorTakenException e) {
      response.status(403);

      return new FailureResponse("Error: already taken");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
