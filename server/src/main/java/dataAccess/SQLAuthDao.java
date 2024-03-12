package dataAccess;

import dataAccessExceptions.DataAccessException;
import dataAccessExceptions.LoginUnauthorizedException;
import dataAccessExceptions.RegisterAlreadyTakenException;
import dataAccessExceptions.RegisterBadRequestException;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class SQLAuthDao implements AuthDAO {
  private final String[] createStatements = {"""
    CREATE TABLE IF NOT EXISTS users (
      id SERIAL PRIMARY KEY,
      username VARCHAR(255) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL,
      email VARCHAR(255) UNIQUE NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """, """
    CREATE TABLE IF NOT EXISTS auth_tokens (
      token_id SERIAL PRIMARY KEY,
      user_id BIGINT UNSIGNED NOT NULL,
      token VARCHAR(255) UNIQUE NOT NULL,
      FOREIGN KEY (user_id) REFERENCES users(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """};

  public SQLAuthDao() {
    try {
      configureDatabase();
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<UserData> getUser(String username) {
    String sql = "SELECT * FROM users WHERE username = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, username);

        var rs = preparedStatement.executeQuery();
        if (rs.next()) {
          return Optional.of(new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email")));
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return Optional.empty();
  }

  private boolean userOrEmailExists(String username, String email) throws SQLException {
    String sql = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, email);

        var rs = preparedStatement.executeQuery();
        rs.next();

        return rs.getInt(1) > 0;
      }
    } catch (DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private void setAuthToken(String username, String token) {
    String sql = "INSERT INTO auth_tokens (user_id, token) SELECT id, ? FROM users WHERE username = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, token);
        preparedStatement.setString(2, username);

        preparedStatement.executeUpdate();
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public LoginResult register(UserData newUser) throws RegisterException {
    String newUsername = newUser.username();
    String newPassword = newUser.password();
    String newEmail = newUser.email();

    if (newUsername == null || newPassword == null || newEmail == null) {
      throw new RegisterBadRequestException("User attempted to register with a null value");
    }

    if (newUsername.length() == 0 || newPassword.length() == 0 || newEmail.length() == 0) {
      throw new RegisterBadRequestException("User attempted to register with an empty value");
    }

    try {
      if (userOrEmailExists(newUsername, newEmail)) {
        throw new RegisterAlreadyTakenException("User attempted to register already taken information");
      }

      String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

      try (var conn = DatabaseManager.getConnection()) {
        try (var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
          preparedStatement.setString(1, newUser.username());
          preparedStatement.setString(2, newUser.password());
          preparedStatement.setString(3, newUser.email());

          int affectedRows = preparedStatement.executeUpdate();

          if (affectedRows == 0) {
            throw new RegisterBadRequestException("Creating user failed, no rows affected");
          }

          String newToken = UUID.randomUUID().toString();
          setAuthToken(newUsername, newToken);

          return new LoginResult(newUsername, newToken);
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public LoginResult login(LoginRequest user) throws LoginException {
    UserData userNotNull = getUser(user.username()).orElseThrow(() -> new LoginUnauthorizedException("User not found within database"));

    if (!userNotNull.password().equals(user.password())) {
      throw new LoginUnauthorizedException("User attempted to login with incorrect password");
    }

    String newToken = UUID.randomUUID().toString();
    setAuthToken(user.username(), newToken);

    return new LoginResult(userNotNull.username(), newToken);
  }

  public void authorize(String authToken) throws LoginUnauthorizedException {
    String sql = "SELECT users.username FROM users JOIN auth_tokens ON users.id = auth_tokens.user_id WHERE auth_tokens.token = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, authToken);

        var rs = preparedStatement.executeQuery();
        if (!rs.next()) {
          throw new LoginUnauthorizedException("User attempted to authorize with incorrect authToken");
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void logout(String authToken) throws LoginUnauthorizedException {
    // verify the authToken is valid (maybe overkill?)
    authorize(authToken);

    String sql = "DELETE FROM auth_tokens WHERE token = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, authToken);
        preparedStatement.executeUpdate();
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public String getUsernameFromToken(String authToken) {
    String sql = "SELECT users.username FROM users JOIN auth_tokens ON users.id = auth_tokens.user_id WHERE auth_tokens.token = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, authToken);

        var rs = preparedStatement.executeQuery();
        if (rs.next()) {
          return rs.getString("username");
        }
      }
    } catch (SQLException | DataAccessException e) {
      throw new RuntimeException(e);
    }

    return "";
  }

  public void clear() {
    try (var conn = DatabaseManager.getConnection()) {
      try (var preparedStatementDisableChecks = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
        preparedStatementDisableChecks.executeUpdate();
      }

      try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE users")) {
        preparedStatement.executeUpdate();
      }

      try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE auth_tokens")) {
        preparedStatement.executeUpdate();
      }

      try (var preparedStatementEnableChecks = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
        preparedStatementEnableChecks.executeUpdate();
      }

      configureDatabase();
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
