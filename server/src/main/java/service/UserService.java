package service;

import model.*;

public class UserService {
  public AuthData register(UserData user) {
    return new AuthData("", "");
  }
  public AuthData login(UserData user) {
    return new AuthData("", "");
  }
  public void logout(UserData user) {}
}
