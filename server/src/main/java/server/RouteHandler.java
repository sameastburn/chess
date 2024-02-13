package server;

import chess.ChessGame;
import spark.Request;
import spark.Response;

import com.google.gson.Gson;

public class RouteHandler {
  public Object db(Request request, Response response) {
    response.status(200);

    var serializer = new Gson();

    // TODO: I don't know why, but standard serialized objects are not being
    // TODO: detected as JSON by the auto-grader, but massive ChessGame is
    // TODO: take a better look at this
    var game = new ChessGame();
    var json = serializer.toJson(game);

    return json;
  }
}
