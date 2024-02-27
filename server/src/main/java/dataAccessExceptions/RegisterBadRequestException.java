package dataAccessExceptions;

import dataAccess.RegisterException;

public class RegisterBadRequestException extends RegisterException {
  public RegisterBadRequestException(String message) {
    super(message);
  }
}
