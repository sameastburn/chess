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

    Optional<UserData> foundUserByUsername = users.stream().filter(user -> user.username().equals(newUsername)).findFirst();
    Optional<UserData> foundUserByEmail = users.stream().filter(user -> user.email().equals(newEmail)).findFirst();

    if (foundUserByUsername.isPresent() || foundUserByEmail.isPresent()) {
      throw new RegisterAlreadyTakenException("User attempted to register already taken information");
    }

    String newToken = UUID.randomUUID().toString();
    authTokens.put(newToken, newUsername);

    users.add(newUser);

    return new LoginResult(newUsername, newToken);
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
    return authTokens.getOrDefault(authToken, "");
  }

  public void clear() {
    users.clear();
    authTokens.clear();
  }
}
