package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 *
 * ChessPosition
 * This represents a location on the chessboard. This should be represented as a row number from 1-8
 * and a column number from 1-8.
 * For example, (1,1) corresponds to the bottom left corner (which in chess notation is denoted a1).
 * (8,8) corresponds to the top right corner (h8 in chess notation).
 *
 */

public class ChessPosition {
    private Integer row;
    private Integer col;

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

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }
}
