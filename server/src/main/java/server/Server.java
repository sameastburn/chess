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

    Spark.before((request, response) -> {
      System.out.println(request.headers("Authorization"));
    });

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

    var webDir = Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "web");
    Spark.externalStaticFileLocation(webDir.toString());

    initRoutes();

    Spark.awaitInitialization();
    return Spark.port();
  }

  public void stop() {
    Spark.stop();
  }
}
