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
    UserData userNotNull = getUser(user.username()).orElseThrow(() -> new LoginUnauthorizedException("User not found within database"));

    if (!userNotNull.password().equals(user.password())) {
      throw new LoginUnauthorizedException("User attempted to login with incorrect password");
    }

    String newToken = UUID.randomUUID().toString();
    authTokens.add(newToken);

    return new LoginResult(userNotNull.username(), newToken);
  }

  public void authorize(String authToken) throws LoginUnauthorizedException {
    if (!authTokens.contains(authToken)) {
      throw new LoginUnauthorizedException("User attempted to authorize with incorrect authToken");
    }
  }
}
