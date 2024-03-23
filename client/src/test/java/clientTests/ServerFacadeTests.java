package clientTests;

import client.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
  private static Server server;
  private static ServerFacade facade;

  @BeforeAll
  public static void init() {
    server = new Server();
    var port = server.run(0);

    System.out.println("Started test HTTP server on " + port);

    facade = ServerFacade.getInstance();
    facade.setPort(port);
  }

  @AfterAll
  static void stopServer() {
    server.stop();
    System.out.println("Stopped test HTTP server");
  }

  @BeforeEach
  void clearDatabase() {
    server.clear();
  }

  public void getInstancePositive()  {
    ServerFacade instance1 = ServerFacade.getInstance();
    ServerFacade instance2 = ServerFacade.getInstance();

    assertSame(instance1, instance2);
  }

  @Test
  public void getInstanceNegative() {
    ServerFacade serverFacadeNegative = ServerFacade.getInstance();

    Assertions.assertNotNull(serverFacadeNegative);
  }

  @Test
  void loginPositive() throws Exception {
    var success = facade.register("player1", "password", "email@email.com");
    assertTrue(success);

    var loginResult = facade.login("player1", "password");
    assertTrue(loginResult);
  }

  @Test
  void loginNegative() throws Exception {
    var registerShouldSucceed = facade.register("login-negative", "password", "email@email.com");
    assertTrue(registerShouldSucceed);

    var loginResultShouldFail = facade.login("login-negative-2", "password");
    assertFalse(loginResultShouldFail);
  }

  @Test
  void registerPositive() throws Exception {
    var success = facade.register("register-should-succeed", "password", "p2@email.com");
    assertTrue(success);
  }

  @Test
  void registerNegative() throws Exception {
    var registerShouldFail = facade.register(null, "password", "p2@email.com");
    assertFalse(registerShouldFail);
  }

  @Test
  void createPositive() throws Exception {
    var success = facade.register("playerForCreate", "password", "email@email.com");
    assertTrue(success);

    var loginResult = facade.login("playerForCreate", "password");
    assertTrue(loginResult);

    var sucessCreateGame = facade.create("niceGame");
    assertTrue(sucessCreateGame);
  }

  @Test
  void createNegative() throws Exception {
    var createShouldFail = facade.create(null);
    assertFalse(createShouldFail);
  }

  @Test
  void listPositive() throws Exception {
    var success = facade.register("playerForList", "password", "email@email.com");
    assertTrue(success);

    var loginResult = facade.login("playerForList", "password");
    assertTrue(loginResult);

    var createForList = facade.create("listGame");
    assertTrue(createForList);

    var gamesList = facade.list();
    assertNotNull(gamesList);
    assertFalse(gamesList.isEmpty());
  }

  @Test
  void listNegative() throws Exception {
    var createForList = facade.create("game-without-authorization");
    assertFalse(createForList);
  }

  @Test
  void joinPositive() throws Exception {
    var success = facade.register("playerForJoin", "password", "email@email.com");
    assertTrue(success);

    var loginResult = facade.login("playerForJoin", "password");
    assertTrue(loginResult);

    var createForJoin = facade.create("forJoin");
    assertTrue(createForJoin);

    var joinSuccess = facade.join("white", 1);
    assertTrue(joinSuccess);
  }

  @Test
  void joinNegative() throws Exception {
    var joinWithoutAuthorization = facade.join("black", 1);
    assertFalse(joinWithoutAuthorization);
  }

  @Test
  void isSuccessfulPositive() {
    var codePositive = facade.isSuccessful(200);
    assertTrue(codePositive);
  }

  @Test
  void isSuccessfulNegative() {
    var codePositive = facade.isSuccessful(404);
    assertFalse(codePositive);
  }
}
