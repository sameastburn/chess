package server;

import com.google.gson.Gson;
import spark.Service;
import spark.Spark;

import java.nio.file.Paths;

import static spark.Service.ignite;

public class Server {
  public int run(int desiredPort) {
    Service http = ignite();

    http.port(desiredPort);

    http.staticFiles.location("web");

    RouteHandler routeHandler = RouteHandler.getInstance();

    // leverage ResponseTransfer to automatically JSON-ify our responses
    // remember to still deserialize requests
    Gson gson = new Gson();

    http.delete("/db", routeHandler::db, gson::toJson);
    http.post("/user", routeHandler::user, gson::toJson);
    http.post("/session", routeHandler::session, gson::toJson);
    http.get("/game", routeHandler::gameGet, gson::toJson);
    http.post("/game", routeHandler::gamePost, gson::toJson);
    http.delete("/session", routeHandler::sessionDelete, gson::toJson);
    http.put("/game", routeHandler::gamePut, gson::toJson);

    http.awaitInitialization();
    return http.port();
  }

  public void stop() {
    Spark.stop();
  }
}
