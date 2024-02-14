package dataAccess;

import model.*;

public interface AuthDAO {
  public LoginResult register(UserData newUser);
  public LoginResult login(LoginRequest user) throws LoginException;
}
