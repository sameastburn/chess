package chess;

import java.util.Arrays;
import java.util.Map;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
  final static Map<Character, ChessPiece.PieceType> charToTypeMap = Map.of('p', ChessPiece.PieceType.PAWN, 'n', ChessPiece.PieceType.KNIGHT, 'r', ChessPiece.PieceType.ROOK, 'q', ChessPiece.PieceType.QUEEN, 'k', ChessPiece.PieceType.KING, 'b', ChessPiece.PieceType.BISHOP);
  private ChessPiece[][] board;

  public ChessBoard() {
    // we will express positions as [1-8]...
    // this is kind of a lame solution, could hack ChessPosition, but
    // this should be good enough for this project
    board = new ChessPiece[9][9];
  }

  /**
   * Adds a chess piece to the chessboard
   *
   * @param position where to add the piece to
   * @param piece    the piece to add
   */
  public void addPiece(ChessPosition position, ChessPiece piece) {
    this.board[position.getRow()][position.getColumn()] = piece;
  }

  /**
   * Gets a chess piece on the chessboard
   *
   * @param position The position to get the piece from
   * @return Either the piece at the position, or null if no piece is at that
   * position
   */
  public ChessPiece getPiece(ChessPosition position) {
    return this.board[position.getRow()][position.getColumn()];
  }

  public boolean hasPieceAt(ChessPosition position) {
    return getPiece(position) != null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChessBoard that = (ChessBoard) o;

    return Arrays.deepEquals(board, that.board);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(board);
  }

  public ChessPiece[][] getBoard() {
    return this.board;
  }

  /**
   * Sets the board to the default starting board
   * (How the game of chess normally starts)
   */
  public void resetBoard() {
    var boardText = ("""
            |r|n|b|q|k|b|n|r|
            |p|p|p|p|p|p|p|p|
            | | | | | | | | |
            | | | | | | | | |
            | | | | | | | | |
            | | | | | | | | |
            |P|P|P|P|P|P|P|P|
            |R|N|B|Q|K|B|N|R|
            """);
    var board = new ChessBoard();
    int row = 8;
    int column = 1;
    for (var c : boardText.toCharArray()) {
      switch (c) {
        case '\n' -> {
          column = 1;
          row--;
        }
        case ' ' -> column++;
        case '|' -> {
        }
        default -> {
          ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
          var type = charToTypeMap.get(Character.toLowerCase(c));
          var position = new ChessPosition(row, column);
          var piece = new ChessPiece(color, type);
          board.addPiece(position, piece);
          column++;
        }
      }
    }
    this.board = board.getBoard();
  }
}
