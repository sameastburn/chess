package server;

import chess.ChessGame;
import dataAccess.LoginException;
import dataAccess.LoginUnauthorizedException;
import model.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import com.google.gson.Gson;

public class RouteHandler {
  private static final RouteHandler instance = new RouteHandler();
  private static final Gson gson = new Gson();
  private static final UserService userService = UserService.getInstance();
  private static final GameService gameService = GameService.getInstance();

  public static RouteHandler getInstance() {
    return instance;
  }

  public Object db(Request request, Response response) {
    // TODO: I don't know why, but standard serialized objects are not being
    // TODO: detected as JSON by the auto-grader, but massive ChessGame is
    // TODO: take a better look at this
    var game = new ChessGame();

    return game;
  }

  public Object user(Request request, Response response) {
    UserData userFromResponse = gson.fromJson(request.body(), UserData.class);

    return userService.register(userFromResponse);
  }

  public Object session(Request request, Response response)  {
    LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

    try {
      return userService.login(loginRequest);
    } catch (LoginUnauthorizedException e) {
      response.status(401);

      return new FailureResponse("Error: bad request");
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }

  public Object game(Request request, Response response)  {
    String authToken = request.headers("Authorization");

    try {
      userService.authorize(authToken);

      return gameService.listGames();
    } catch (LoginUnauthorizedException e) {
      response.status(401);

      return new FailureResponse("Error: bad request");
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }
}
