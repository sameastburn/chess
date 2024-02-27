package dataAccessExceptions;

import dataAccess.LoginException;

public class LoginUnauthorizedException extends LoginException {
  public LoginUnauthorizedException(String message) {
    super(message);
  }
}
