package passoffTests.serverTests;

import dataAccess.LoginException;
import dataAccessExceptions.LoginUnauthorizedException;
import dataAccess.RegisterException;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
  @BeforeEach
  public void setup() {
    UserService.getInstance().clear();
  }

  @Test
  public void getInstancePositive()  {
    UserService instance1 = UserService.getInstance();
    UserService instance2 = UserService.getInstance();

    assertSame(instance1, instance2, "Multiple calls to getInstance() did not return the same instance");
  }

  @Test
  public void getInstanceNegative() {
    UserService userService = UserService.getInstance();

    Assertions.assertNotNull(userService, "getInstance() returned null");
  }

  @Test
  public void registerPositive() throws RegisterException {
    UserService userService = UserService.getInstance();

    UserData newUser = new UserData("username", "password", "email");
    LoginResult registerResult = userService.register(newUser);

    Assertions.assertTrue(registerResult.username() == "username", "Register didn't return username");
  }

  @Test
  public void registerNegative() throws RegisterException {
    UserService userService = UserService.getInstance();

    UserData newUser = new UserData("username", "password", "email");
    LoginResult registerResult = userService.register(newUser);

    Assertions.assertFalse(registerResult.username() == "not-the-username", "Register didn't return username");
  }

  @Test
  public void loginPositive() throws RegisterException, LoginException {
    UserService userService = UserService.getInstance();

    UserData newUser = new UserData("username", "password", "email");
    LoginResult registerResult = userService.register(newUser);

    LoginRequest loginRequest = new LoginRequest("username", "password");
    LoginResult result = userService.login(loginRequest);

    Assertions.assertEquals("username", result.username(), "Login didn't return the same username");
  }

  @Test
  public void loginNegative() throws RegisterException, LoginException {
    UserService userService = UserService.getInstance();

    UserData newUser = new UserData("username", "password", "email");
    LoginResult registerResult = userService.register(newUser);

    LoginRequest loginRequest = new LoginRequest("username", "bad-password");

    assertThrows(LoginException.class, () -> userService.login(loginRequest),"Login with incorrect password didn't return an exception");
  }

  @Test
  public void authorizePositive() throws LoginUnauthorizedException, RegisterException {
    UserService userService = UserService.getInstance();
    UserData newUser = new UserData("username", "password", "email");

    Assertions.assertDoesNotThrow(() -> { userService.register(newUser); }, "Register threw an exception when it shouldn't");
  }

  @Test
  public void authorizeNegative() {
    UserService userService = UserService.getInstance();
    UserData newUser = new UserData("", "", "");

    Assertions.assertThrows(RegisterException.class, () -> { userService.register(newUser); }, "Expected RegisterException to be thrown with empty values, but it wasn't");
  }

  @Test
  public void logoutPositive() throws RegisterException, LoginUnauthorizedException {
    UserService userService = UserService.getInstance();

    UserData newUser = new UserData("username", "password", "email");
    LoginResult registerResult = userService.register(newUser);

    userService.logout(registerResult.authToken());

    Assertions.assertTrue(userService.getUsernameFromToken(registerResult.authToken()) == "", "Token should have been empty");
  }

}
