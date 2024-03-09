package dataAccess;

import chess.ChessGame;
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
      gameState TEXT,
      CONSTRAINT UC_Game UNIQUE (whiteUsername, blackUsername)
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
        var rs = preparedStatement.executeQuery(sql);

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

  private Optional<GameData> findGame(int gameID) {
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

        Gson gson = new Gson();
        preparedStatement.setString(2, gson.toJson(new ChessGame()));

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

    String sqlUpdate = "UPDATE games SET %s = ? WHERE gameID = ? AND %s IS NULL";
    String columnToUpdate = joinGameRequest.playerColor().equals("WHITE") ? "whiteUsername" : "blackUsername";
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

  public void clear() {
    games.clear();

    String sql = "TRUNCATE TABLE games";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.executeUpdate();
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

/*
    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {

      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

 */