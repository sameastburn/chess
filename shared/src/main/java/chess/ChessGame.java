package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
  private ChessBoard board;
  private TeamColor turn;

  public ChessGame() {

  }

  /**
   * @return Which team's turn it is
   */
  public TeamColor getTeamTurn() {
    return this.turn;
  }

  /**
   * Set's which teams turn it is
   *
   * @param team the team whose turn it is
   */
  public void setTeamTurn(TeamColor team) {
    this.turn = team;
  }

  /**
   * Gets a valid moves for a piece at the given location
   *
   * @param startPosition the piece to get valid moves for
   * @return Set of valid moves for requested piece, or null if no piece at
   * startPosition
   */
  public Collection<ChessMove> validMoves(ChessPosition startPosition) {
    var piece = board.getPiece(startPosition);

    return piece.pieceMoves(this.board, startPosition);
  }

  private boolean isMoveWithinTurn(ChessMove move) {
    var piece = board.getPiece(move.getStartPosition());

    return piece.getTeamColor() == getTeamTurn();
  }

  /**
   * Makes a move in a chess game
   *
   * @param move chess move to preform
   * @throws InvalidMoveException if move is invalid
   */
  public void makeMove(ChessMove move) throws InvalidMoveException {
    if (!isMoveWithinTurn(move)) {
      throw new InvalidMoveException("*** DEBUG *** InvalidMoveException (!isMoveWithinTurn(move))");
    }

    var startPosition = move.getStartPosition();
    var validMoves = validMoves(startPosition);

    if (!validMoves.contains(move)) {
      throw new InvalidMoveException("*** DEBUG *** InvalidMoveException (!validMoves.contains(move))");
    }

    var tempBoard = new ChessBoard(board.getBoard());
    tempBoard.movePiece(move.getStartPosition(), move.getEndPosition());

    if (isInCheck(getTeamTurn(), tempBoard)) {
      throw new InvalidMoveException("*** DEBUG *** InvalidMoveException didn't get out of check");
    }

    board.movePiece(move.getStartPosition(), move.getEndPosition());

    setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
  }

  /**
   * Determines if the given team is in check
   *
   * @param teamColor which team to check for check
   * @return True if the specified team is in check
   */

  public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
    var oldBoard = getBoard();

    // can't alter signature of the existing methods... hacky ftw
    setBoard(board);
    var ret = isInCheck(teamColor);
    setBoard(oldBoard);

    return ret;
  }

  public boolean isInCheck(TeamColor teamColor) {
    boolean inCheck = false;

    var board = getBoard();

    ChessPosition friendlyKingPosition = new ChessPosition(0, 0);

    ArrayList<ChessMove> allEnemyPossibleMoves = new ArrayList<>();
    for (int i = 1; i < 9; i++) {
      for (int j = 1; j < 9; j++) {
        var checkPosition = new ChessPosition(i, j);
        var possiblePiece = board.getPiece(checkPosition);

        // premature optimization is beauty! so no existsAt.
        if (possiblePiece != null) {
          if (possiblePiece.getTeamColor() == teamColor) {
            if (possiblePiece.getPieceType() == ChessPiece.PieceType.KING) {
              friendlyKingPosition = checkPosition;
            }

            continue;
          }

          allEnemyPossibleMoves.addAll(possiblePiece.pieceMoves(board, checkPosition));

          // System.out.println("*** DEBUG *** possiblePiece: " + possiblePiece.getTeamColor());
        }
      }
    }

    // check if any of the possible enemy moves include the king
    for (ChessMove move : allEnemyPossibleMoves) {
      // System.out.println("*** DEBUG *** move.row: " + move.getEndPosition().getRow());
      // System.out.println("*** DEBUG *** move.col: " + move.getEndPosition().getColumn() + "\n");

      if (move.getEndPosition().equals(friendlyKingPosition)) {
        // System.out.println("*** DEBUG *** uh oh check detected!");

        inCheck = true;
        break;
      }
    }

    return inCheck;
  }

  /**
   * Determines if the given team is in checkmate
   *
   * @param teamColor which team to check for checkmate
   * @return True if the specified team is in checkmate
   */
  public boolean isInCheckmate(TeamColor teamColor) {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Determines if the given team is in stalemate, which here is defined as having
   * no valid moves
   *
   * @param teamColor which team to check for stalemate
   * @return True if the specified team is in stalemate, otherwise false
   */
  public boolean isInStalemate(TeamColor teamColor) {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Gets the current chessboard
   *
   * @return the chessboard
   */
  public ChessBoard getBoard() {
    return this.board;
  }

  /**
   * Sets this game's chessboard with a given board
   *
   * @param board the new board to use
   */
  public void setBoard(ChessBoard board) {
    this.board = board;
  }

  /**
   * Enum identifying the 2 possible teams in a chess game
   */
  public enum TeamColor {
    WHITE, BLACK
  }
}
