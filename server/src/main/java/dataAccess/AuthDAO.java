package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
  public AuthData register(UserData newUser);
}
