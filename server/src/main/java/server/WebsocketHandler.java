package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
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
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;

@WebSocket
public class WebsocketHandler {
  private static final WebsocketHandler instance = new WebsocketHandler();
  private static final Gson gson = new Gson();
  private static final UserService userService = UserService.getInstance();
  private static final GameService gameService = GameService.getInstance();
  private static final ConnectionManager connectionManager = new ConnectionManager();
  private static final HashSet<Integer> resignedGames = new HashSet<Integer>();

  public static WebsocketHandler getInstance() {
    return instance;
  }

  public void sendLoadGame(Session session, GameData game) throws IOException {
    LoadGameMessage loadGameMessage = new LoadGameMessage();
    loadGameMessage.game = game;

    session.getRemote().sendString(gson.toJson(loadGameMessage));
  }

  public void broadcastLoadGame(GameData game) throws IOException {
    LoadGameMessage loadGameMessage = new LoadGameMessage();
    loadGameMessage.game = game;

    connectionManager.broadcast("", game.gameID, loadGameMessage);
  }

  public void sendNotification(String excludedUser, int gameID, String message) throws IOException {
    NotificationMessage notificationMessage = new NotificationMessage();
    notificationMessage.message = message;

    connectionManager.broadcast(excludedUser, gameID, notificationMessage);
  }

  public void sendError(Session session, String message) throws IOException {
    ErrorMessage errorMessage = new ErrorMessage();
    errorMessage.errorMessage = "Error: " + message;

    session.getRemote().sendString(gson.toJson(errorMessage));
  }

  public void clearResignedGames() {
    resignedGames.clear();
  }

  public void runCheckTests(ChessGame game, String username, int gameID) throws IOException {
    if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
      sendNotification(username, gameID, "Black is in check");
    }

    if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
      sendNotification(username, gameID, "White is in checkmate");
    }

    if (game.isInCheckmate(ChessGame.TeamColor.BLACK )) {
      sendNotification(username, gameID, "Black is in checkmate");
    }
  }

  @OnWebSocketMessage
  public void onMessage(Session session, String message) throws Exception {
    try {
      UserGameCommand gameCommand = gson.fromJson(message, UserGameCommand.class);
      String authString = gameCommand.getAuthString();
      String username = userService.getUsernameFromToken(gameCommand.getAuthString());

      userService.authorize(authString);

      switch (gameCommand.getCommandType()) {
        case JOIN_PLAYER: {
          JoinPlayerCommand joinCommand = gson.fromJson(message, JoinPlayerCommand.class);
          Integer gameID = joinCommand.gameID;
          ChessGame.TeamColor playerColor = joinCommand.playerColor;

          GameData gameNotNull = gameService.findGame(gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to join a nonexistent game"));

          if (gameNotNull.whiteUsername == null && gameNotNull.blackUsername == null) {
            throw new RuntimeException("Trying to join game before HTTP request!");
          }

          if (playerColor == ChessGame.TeamColor.WHITE) {
            if (gameNotNull.whiteUsername != null && !gameNotNull.whiteUsername.equals(username)) {
              throw new RuntimeException("White spot already taken!");
            }
          } else if (playerColor == ChessGame.TeamColor.BLACK) {
            if (gameNotNull.blackUsername != null && !gameNotNull.blackUsername.equals(username)) {
              throw new RuntimeException("Black spot already taken!");
            }
          }

          connectionManager.add(username, session, gameID);

          sendLoadGame(session, gameNotNull);
          sendNotification(username, gameNotNull.gameID, username + " joined game '" + joinCommand.gameID + "' as '" + playerColor + "'");

          break;
        }
        case JOIN_OBSERVER: {
          JoinObserverCommand joinCommand = gson.fromJson(message, JoinObserverCommand.class);
          Integer gameID = joinCommand.gameID;

          connectionManager.add(username, session, gameID);

          GameData gameNotNull = gameService.findGame(joinCommand.gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to join a nonexistent game"));

          sendLoadGame(session, gameNotNull);
          sendNotification(username, gameID, username + " is now observing game '" + joinCommand.gameID + "'");

          break;
        }
        case MAKE_MOVE: {
          MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
          Integer gameID = moveCommand.gameID;

          GameData gameNotNull = gameService.findGame(gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to make a move on a nonexistent game"));

          if (resignedGames.contains(gameID)) {
            throw new RuntimeException("Attempted to make a move in a game with a resignation");
          }

          ChessPiece piece = gameNotNull.game.getBoard().getPiece(moveCommand.move.getStartPosition());

          if (piece == null) {
            throw new RuntimeException("Early detection of an invalid move");
          }

          ChessGame.TeamColor pieceColor = piece.getTeamColor();

          // check if the player is trying to move for opponent
          // this could probably be abstracted into gameService but this is the solution for now
          if (pieceColor == ChessGame.TeamColor.WHITE) {
            if (gameNotNull.whiteUsername != null && !gameNotNull.whiteUsername.equals(username)) {
              throw new RuntimeException("Player with black pieces tried to move for opponent!");
            }
          } else if (pieceColor == ChessGame.TeamColor.BLACK) {
            if (gameNotNull.blackUsername != null && !gameNotNull.blackUsername.equals(username)) {
              throw new RuntimeException("Player with white pieces tried to move for opponent!");
            }
          }

          gameService.makeMove(gameID, moveCommand.move);

          GameData updatedGame = gameService.findGame(gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to make a move on a nonexistent game"));
          broadcastLoadGame(updatedGame);

          sendNotification(username, gameID, username + " made a move");

          runCheckTests(updatedGame.game, username, gameID);

          break;
        } case RESIGN: {
          ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
          int gameID = resignCommand.gameID;

          GameData gameNotNull = gameService.findGame(resignCommand.gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to make a move on a nonexistent game"));
          if (!(gameNotNull.whiteUsername.equals(username) || gameNotNull.blackUsername.equals(username))) {
            throw new RuntimeException("Player not in game or observer tried to resign");
          }

          if (resignedGames.contains(gameID)) {
            throw new RuntimeException("Player tried to resign in a game that was already finished");
          }

          sendNotification("", gameID, username + " resigned");

          resignedGames.add(gameID);

          break;
        } case LEAVE: {
          LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);

          connectionManager.remove(username);

          sendNotification(username, leaveCommand.gameID,username + " left the game");

          try {
            gameService.leaveGame(leaveCommand.gameID, username);
          } catch (Exception e) {
            // this might throw because of the observer...
          }

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
