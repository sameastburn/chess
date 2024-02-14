package dataAccess;

import model.LoginRequest;
import model.LoginResult;
import model.UserData;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO {
  private final List<UserData> users = new ArrayList<>();
  private final HashSet<String> authTokens = new HashSet<String>();

  private Optional<UserData> getUser(String username) {
    Optional<UserData> foundUser = users.stream().filter(user -> user.username().equals(username)).findFirst();

    return foundUser;
  }

  @Override
  public LoginResult register(UserData newUser) {
    users.add(newUser);

    return new LoginResult(newUser.username(), "");
  }

  @Override
  public LoginResult login(LoginRequest user) throws LoginException {
    var userFromDatabase = getUser(user.username());

    if (userFromDatabase.isPresent()) {
      String newToken = UUID.randomUUID().toString();
      authTokens.add(newToken);

      return new LoginResult(user.username(), newToken);
    } else {
      throw new LoginUnauthorizedException("User not found within database");
    }
  }
}
