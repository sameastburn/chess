package dataAccess;

import model.*;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO {
  private List<UserData> users = new ArrayList<>();
  private HashSet<String> authTokens = new HashSet<String>();

  @Override
  public LoginResult register(UserData newUser) {
    users.add(newUser);

    return new LoginResult(newUser.username(), "");
  }

  @Override
  public LoginResult login(LoginRequest user) {
    String newToken = UUID.randomUUID().toString();
    authTokens.add(newToken);

    return new LoginResult(user.username(), newToken);
  }
}
