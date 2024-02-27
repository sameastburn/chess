package dataAccessExceptions;

import dataAccess.GameException;

public class GameColorTakenException extends GameException {
  public GameColorTakenException(String message) {
    super(message);
  }
}
