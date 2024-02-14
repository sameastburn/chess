package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO {
  private List<UserData> users = new ArrayList<>();
  private HashSet<String> authTokens = new HashSet<String>();

  @Override
  public AuthData register(UserData newUser) {
    users.add(newUser);

    // TODO: for now we just need to know if the token exists...
    // TODO: probably upgrade to a map or something
    String newToken = UUID.randomUUID().toString();
    authTokens.add(newToken);

    return new AuthData(newToken, newUser.username());
  }
}
