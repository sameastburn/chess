package dataAccessExceptions;

import dataAccess.GameException;

public class GameBadGameIDException extends GameException {
  public GameBadGameIDException(String message) {
    super(message);
  }
}
