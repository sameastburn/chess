package dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.LoginException;
import dataAccess.RegisterException;
import dataAccess.SQLAuthDao;
import dataAccessExceptions.LoginUnauthorizedException;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {
  private static final AuthDAO authDAO = new SQLAuthDao();
  private UserData newUser;
  private LoginRequest loginRequest;

  @BeforeEach
  public void setup() {
    authDAO.clear();
    newUser = new UserData("username", "password", "email");
    loginRequest = new LoginRequest("username", "password");
  }

  @Test
  public void registerPositive() throws RegisterException {
    LoginResult result = authDAO.register(newUser);

    Assertions.assertEquals("username", result.username(), "Register didn't return correct username!");
  }

  @Test
  public void registerNegative() {
    Assertions.assertThrows(RegisterException.class, () -> {
      authDAO.register(newUser);
      authDAO.register(newUser);
    }, "Expected RegisterException was not thrown when registering twice with same information!");
  }

  @Test
  public void loginPositive() throws RegisterException, LoginException {
    authDAO.register(newUser);
    LoginResult result = authDAO.login(loginRequest);

    Assertions.assertEquals("username", result.username(), "Login didn't return correct username!");
  }

  @Test
  public void loginNegative() {
    Assertions.assertThrows(LoginException.class, () -> authDAO.login(loginRequest), "Expected LoginException was not thrown for invalid credentials!");
  }

  @Test
  public void logoutPositive() throws RegisterException, LoginException {
    authDAO.register(newUser);
    LoginResult result = authDAO.login(loginRequest);

    authDAO.logout(result.authToken());
    Assertions.assertThrows(LoginUnauthorizedException.class, () -> authDAO.authorize(result.authToken()), "Logout did not properly invalidate the auth token!");
  }

  @Test
  public void logoutNegative() {
    Assertions.assertThrows(LoginUnauthorizedException.class, () -> authDAO.logout("invalidToken"), "Expected LoginUnauthorizedException was not thrown for invalid token!");
  }

  @Test
  public void authorizePositive() throws RegisterException, LoginException {
    authDAO.register(newUser);

    LoginResult result = authDAO.login(loginRequest);
    Assertions.assertDoesNotThrow(() -> authDAO.authorize(result.authToken()), "Authorize failed for a valid token!");
  }

  @Test
  public void authorizeNegative() {
    Assertions.assertThrows(LoginUnauthorizedException.class, () -> authDAO.authorize("invalidToken"), "Expected LoginUnauthorizedException was not thrown for invalid token!");
  }

  @Test
  public void getUsernameFromTokenPositive() throws RegisterException, LoginException {
    authDAO.register(newUser);

    LoginResult result = authDAO.login(loginRequest);
    String username = authDAO.getUsernameFromToken(result.authToken());

    Assertions.assertEquals("username", username, "getUsernameFromToken did not return the expected username!");
  }

  @Test
  public void getUsernameFromTokenNegative() {
    String username = authDAO.getUsernameFromToken("invalidToken");

    Assertions.assertTrue(username.isEmpty(), "getUsernameFromToken should return an empty string for an invalid token!");
  }

  @Test
  public void clearPositive() {
    Assertions.assertDoesNotThrow(() -> authDAO.clear(), "Clear method should not throw any exception!");
  }
}
