package dataAccess;

public class RegisterAlreadyTakenException extends RegisterException {
  public RegisterAlreadyTakenException(String message) {
    super(message);
  }
}
