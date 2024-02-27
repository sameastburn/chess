package server;

import com.google.gson.Gson;
import spark.Spark;

import java.nio.file.Paths;

public class Server {
  private void initRoutes() {
    RouteHandler routeHandler = RouteHandler.getInstance();

    // leverage ResponseTransfer to automatically JSON-ify our responses
    // remember to still deserialize requests
    Gson gson = new Gson();

    Spark.delete("/db", routeHandler::db, gson::toJson);
    Spark.post("/user", routeHandler::user, gson::toJson);
    Spark.post("/session", routeHandler::session, gson::toJson);
    Spark.get("/game", routeHandler::gameGet, gson::toJson);
    Spark.post("/game", routeHandler::gamePost, gson::toJson);
    Spark.delete("/session", routeHandler::sessionDelete, gson::toJson);
    Spark.put("/game", routeHandler::gamePut, gson::toJson);
  }

  public int run(int desiredPort) {
    Spark.port(desiredPort);

    Spark.staticFiles.location("web");

    initRoutes();

    Spark.awaitInitialization();
    return Spark.port();
  }

  public void stop() {
    Spark.stop();
  }
}
