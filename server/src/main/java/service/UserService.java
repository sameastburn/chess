package service;

import dataAccess.AuthDAO;
import dataAccess.LoginException;
import dataAccess.LoginUnauthorizedException;
import dataAccess.MemoryAuthDAO;
import model.*;

public class UserService {
  private static final UserService instance = new UserService();
  private static final AuthDAO authDAO = new MemoryAuthDAO();

  public static UserService getInstance() {
    return instance;
  }

  public LoginResult register(UserData user) {
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
}
