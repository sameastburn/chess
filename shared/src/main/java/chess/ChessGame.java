package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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

  public static KingReturn findFriendlyKing(ChessBoard board, TeamColor teamColor) {
    ChessPosition friendlyKingPosition = new ChessPosition(0, 0);
    ChessPiece friendlyKingPiece = new ChessPiece(teamColor, ChessPiece.PieceType.KING);

    for (int i = 1; i < 9; i++) {
      for (int j = 1; j < 9; j++) {
        var checkPosition = new ChessPosition(i, j);
        var possiblePiece = board.getPiece(checkPosition);

        if (possiblePiece != null) {
          if (possiblePiece.getTeamColor() == teamColor && possiblePiece.getPieceType() == ChessPiece.PieceType.KING) {
            friendlyKingPosition = checkPosition;
            friendlyKingPiece = possiblePiece;
          }
        }
      }
    }

    return new ChessGame.KingReturn(friendlyKingPosition, friendlyKingPiece);
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
    var teamColor = piece.getTeamColor();
    var potentialMoves = piece.pieceMoves(this.board, startPosition);
    var validMoves = new HashSet<ChessMove>();

    // filter moves that leave the king in danger
    for (ChessMove move : potentialMoves) {
      ChessBoard tempBoard = new ChessBoard(board.getBoard());
      tempBoard.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());

      if (!isInCheck(teamColor, tempBoard)) {
        validMoves.add(move);
      }
    }

    return validMoves;
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
    tempBoard.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());

    if (isInCheck(getTeamTurn(), tempBoard)) {
      throw new InvalidMoveException("*** DEBUG *** InvalidMoveException didn't get out of check");
    }

    board.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());

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
        }
      }
    }

    // check if any of the possible enemy moves include the king
    for (ChessMove move : allEnemyPossibleMoves) {
      if (move.getEndPosition().equals(friendlyKingPosition)) {
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
    if (!isInCheck(teamColor)) {
      return false;
    }

    KingReturn kingInfo = findFriendlyKing(board, teamColor);

    Collection<ChessMove> possibleKingMoves = kingInfo.piece().pieceMoves(getBoard(), kingInfo.position());
    var tempBoard = new ChessBoard(board.getBoard());

    boolean escapedCheck = false;
    for (ChessMove move : possibleKingMoves) {
      tempBoard.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());

      if (!isInCheck(teamColor, tempBoard)) {
        escapedCheck = true;

        break;
      }

      tempBoard.setBoard(board.getBoard());
    }

    return !escapedCheck;
  }

  /**
   * Determines if the given team is in stalemate, which here is defined as having
   * no valid moves
   *
   * @param teamColor which team to check for stalemate
   * @return True if the specified team is in stalemate, otherwise false
   */
  public boolean isInStalemate(TeamColor teamColor) {
    // stalemate happens if two conditions are true
    // 1. king is not in check
    // 2. no legal moves possible

    if (isInCheck(teamColor)) {
      return false;
    }

    for (int i = 1; i < 9; i++) {
      for (int j = 1; j < 9; j++) {
        var checkPosition = new ChessPosition(i, j);
        var possiblePiece = board.getPiece(checkPosition);

        // take into account moves that are illegal due to leaving the king in danger
        if (possiblePiece != null) {
          if (possiblePiece.getTeamColor() == teamColor) {
            Collection<ChessMove> possibleMoves = possiblePiece.pieceMoves(board, checkPosition);

            for (ChessMove move : possibleMoves) {
              ChessBoard tempBoard = new ChessBoard(board.getBoard());
              tempBoard.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());

              if (!isInCheck(teamColor, tempBoard)) {
                return false;
              }
            }
          }
        }
      }
    }

    return true;
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

  public record KingReturn(ChessPosition position, ChessPiece piece) {
  }
}
