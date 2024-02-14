package dataAccess;

public class LoginUnauthorizedException extends LoginException {
  public LoginUnauthorizedException(String message) {
    super(message);
  }
}
