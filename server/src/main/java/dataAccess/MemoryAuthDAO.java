package dataAccess;

import model.LoginRequest;
import model.LoginResult;
import model.UserData;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO {
  private final List<UserData> users = new ArrayList<>();
  private final Map<String, String> authTokens = new HashMap<>();

  private Optional<UserData> getUser(String username) {
    Optional<UserData> foundUser = users.stream().filter(user -> user.username().equals(username)).findFirst();

    return foundUser;
  }

  @Override
  public LoginResult register(UserData newUser) {
    users.add(newUser);

    String newToken = UUID.randomUUID().toString();
    authTokens.put(newToken, newUser.username());

    return new LoginResult(newUser.username(), newToken);
  }

  @Override
  public LoginResult login(LoginRequest user) throws LoginException {
    UserData userNotNull = getUser(user.username()).orElseThrow(() -> new LoginUnauthorizedException("User not found within database"));

    if (!userNotNull.password().equals(user.password())) {
      throw new LoginUnauthorizedException("User attempted to login with incorrect password");
    }

    String newToken = UUID.randomUUID().toString();
    authTokens.put(newToken, user.username());

    return new LoginResult(userNotNull.username(), newToken);
  }

  public void authorize(String authToken) throws LoginUnauthorizedException {
    if (!authTokens.containsKey(authToken)) {
      throw new LoginUnauthorizedException("User attempted to authorize with incorrect authToken");
    }
  }

  public void logout(String authToken) throws LoginUnauthorizedException {
    if (!authTokens.containsKey(authToken)) {
      throw new LoginUnauthorizedException("User attempted to logout with incorrect authToken");
    }

    authTokens.remove(authToken);
  }

  public String getUsernameFromToken(String authToken) {
    return authTokens.get(authToken);
  }
}
