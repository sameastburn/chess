package dataAccessExceptions;

import dataAccess.RegisterException;

public class RegisterAlreadyTakenException extends RegisterException {
  public RegisterAlreadyTakenException(String message) {
    super(message);
  }
}
