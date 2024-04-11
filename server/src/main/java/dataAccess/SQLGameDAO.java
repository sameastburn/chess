package dataAccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccessExceptions.DataAccessException;
import dataAccessExceptions.GameBadGameIDException;
import dataAccessExceptions.GameColorTakenException;
import model.GameData;
import model.JoinGameRequest;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

public class SQLGameDAO implements GameDAO {
  private final String[] createStatements = {"""
    CREATE TABLE IF NOT EXISTS games (
      gameID INT AUTO_INCREMENT PRIMARY KEY,
      gameName VARCHAR(255) NOT NULL,
      whiteUsername VARCHAR(255),
      blackUsername VARCHAR(255),
      gameState TEXT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """};
  ArrayList<GameData> games = new ArrayList<>();

  public SQLGameDAO() {
    try {
      configureDatabase();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ArrayList<GameData> listGames() {
    ArrayList<GameData> games = new ArrayList<>();
    String sql = "SELECT * FROM games";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        var rs = preparedStatement.executeQuery();

        while (rs.next()) {
          Gson gson = new Gson();
          var gameData = gson.fromJson(rs.getString("gameState"), ChessGame.class);

          GameData game = new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), gameData);
          games.add(game);
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return games;
  }

  public Optional<GameData> findGame(int gameID) {
    String sql = "SELECT * FROM games WHERE gameID = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setInt(1, gameID);

        var rs = preparedStatement.executeQuery();
        if (rs.next()) {
          Gson gson = new Gson();
          var gameData = gson.fromJson(rs.getString("gameState"), ChessGame.class);

          GameData game = new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), gameData);
          return Optional.of(game);
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return Optional.empty();
  }

  public int createGame(String gameName) {
    String sql = "INSERT INTO games (gameName, gameState) VALUES (?, ?)";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        preparedStatement.setString(1, gameName);

        ChessGame newChessGame = new ChessGame();
        newChessGame.getBoard().resetBoard();

        Gson gson = new Gson();
        preparedStatement.setString(2, gson.toJson(newChessGame));

        preparedStatement.executeUpdate();

        try (var keys = preparedStatement.getGeneratedKeys()) {
          if (keys.next()) {
            return keys.getInt(1);
          } else {
            throw new DataAccessException("Couldn't access new gameID when creating game!");
          }
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void joinGame(String username, JoinGameRequest joinGameRequest) throws GameException {
    GameData gameNotNull = findGame(joinGameRequest.gameID()).orElseThrow(() -> new GameBadGameIDException("User attempted to join a nonexistent game"));

    // observers
    if (joinGameRequest.playerColor() == null) {
      return;
    }

    String sqlUpdate = "UPDATE games SET %s = ? WHERE gameID = ? AND %s IS NULL";
    String columnToUpdate = joinGameRequest.playerColor().toLowerCase().equals("white") ? "whiteUsername" : "blackUsername";
    String sql = String.format(sqlUpdate, columnToUpdate, columnToUpdate);

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, username);
        preparedStatement.setInt(2, joinGameRequest.gameID());

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
          throw new GameColorTakenException("User attempted to join a game with a taken color");
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void makeMove(int gameID, ChessMove move) throws GameException, InvalidMoveException {
    GameData gameNotNull = findGame(gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to make a move in a nonexistent game"));
    ChessGame chessGame = gameNotNull.game;

    chessGame.makeMove(move);

    Gson gson = new Gson();
    String updatedGameState = gson.toJson(chessGame);

    String sql = "UPDATE games SET gameState = ? WHERE gameID = ?";
    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, updatedGameState);
        preparedStatement.setInt(2, gameID);

        int affectedRows = preparedStatement.executeUpdate();

        if (affectedRows == 0) {
          throw new DataAccessException("Updating game state failed, no rows affected");
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void leaveGame(int gameID, String username) throws GameException {
    GameData gameNotNull = findGame(gameID).orElseThrow(() -> new GameBadGameIDException("User attempted to leave a nonexistent game"));

    String sql;
    if (username.equals(gameNotNull.whiteUsername) && (username.equals(gameNotNull.blackUsername))) {
      throw new RuntimeException("User attempted to join a game with their previous zombie user");
    } else if (username.equals(gameNotNull.whiteUsername)) {
      sql = "UPDATE games SET whiteUsername = NULL WHERE gameID = ?";
    } else if (username.equals(gameNotNull.blackUsername)) {
      sql = "UPDATE games SET blackUsername = NULL WHERE gameID = ?";
    } else {
      throw new RuntimeException("Username is not part of this game");
    }

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setInt(1, gameID);

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
          throw new RuntimeException("The player was not in the game or already removed");
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void clear() {
    games.clear();

    String sql = "TRUNCATE TABLE games";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatementDisableChecks = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
        preparedStatementDisableChecks.executeUpdate();
      }

      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.executeUpdate();
      }

      try (var preparedStatementEnableChecks = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
        preparedStatementEnableChecks.executeUpdate();
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private void configureDatabase() throws DataAccessException {
    DatabaseManager.createDatabase();

    try (var conn = DatabaseManager.getConnection()) {
      for (var statement : createStatements) {
        try (var preparedStatement = conn.prepareStatement(statement)) {
          preparedStatement.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
