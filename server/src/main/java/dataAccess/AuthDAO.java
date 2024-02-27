package dataAccess;

import dataAccessExceptions.LoginUnauthorizedException;
import model.*;

public interface AuthDAO {
  public LoginResult register(UserData newUser) throws RegisterException;
  public LoginResult login(LoginRequest user) throws LoginException;
  public void logout(String authToken) throws LoginUnauthorizedException;
  public void authorize(String authToken) throws LoginUnauthorizedException;
  String getUsernameFromToken(String authToken);
  public void clear();
}
