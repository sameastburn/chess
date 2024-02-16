package service;

import dataAccess.*;
import model.*;

public class UserService {
  private static final UserService instance = new UserService();
  private static final AuthDAO authDAO = new MemoryAuthDAO();

  public static UserService getInstance() {
    return instance;
  }

  public LoginResult register(UserData user) throws RegisterException {
    return authDAO.register(user);
  }
  public LoginResult login(LoginRequest loginRequest) throws LoginException {
    return authDAO.login(loginRequest);
  }
  public void authorize(String authToken) throws LoginUnauthorizedException {
    authDAO.authorize(authToken);
  }
  public void logout(String authToken) throws LoginUnauthorizedException {
    authDAO.logout(authToken);
  }

  public String getUsernameFromToken(String authToken) {
    return authDAO.getUsernameFromToken(authToken);
  }
}
