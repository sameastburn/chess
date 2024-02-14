package service;

import dataAccess.AuthDAO;
import dataAccess.MemoryAuthDAO;
import model.*;

public class UserService {
  private static final UserService instance = new UserService();
  private static final AuthDAO authDAO = new MemoryAuthDAO();

  public static UserService getInstance() {
    return instance;
  }

  public AuthData register(UserData user) {
    return authDAO.register(user);
  }
  public AuthData login(UserData user) {
    return new AuthData("", "");
  }
  public void logout(UserData user) {}
}
