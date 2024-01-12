package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 * <p>
 * ChessPosition
 * This represents a location on the chessboard. This should be represented as a row number from 1-8
 * and a column number from 1-8.
 * For example, (1,1) corresponds to the bottom left corner (which in chess notation is denoted a1).
 * (8,8) corresponds to the top right corner (h8 in chess notation).
 */

public class ChessPosition {
  private final Integer row;
  private final Integer col;

  public ChessPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }

  /**
   * @return which row this position is in
   * 1 codes for the bottom row
   */
  public int getRow() {
    return this.row;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChessPosition that = (ChessPosition) o;
    return row.equals(that.row) && col.equals(that.col);
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }

  /**
   * @return which column this position is in
   * 1 codes for the left row
   */
  public int getColumn() {
    return this.col;
  }
}
