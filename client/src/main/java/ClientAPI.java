public class ClientAPI {
  private static final ClientAPI instance = new ClientAPI();

  public static ClientAPI getInstance() {
    return instance;
  }

  public boolean login(String username, String password) {
    return username.equals("test") && password.equals("test");
  }
}
