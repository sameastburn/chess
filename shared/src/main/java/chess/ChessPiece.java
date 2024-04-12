package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
  private final ChessGame.TeamColor color;
  private ChessPiece.PieceType type;

  public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
    this.color = pieceColor;
    this.type = type;
  }

  private static void pawnAddPromotionMoves(HashSet<ChessMove> moves, ChessPosition myPosition, ChessPosition newEndPosition) {
    moves.add(new ChessMove(myPosition, newEndPosition, ChessPiece.PieceType.QUEEN));
    moves.add(new ChessMove(myPosition, newEndPosition, ChessPiece.PieceType.BISHOP));
    moves.add(new ChessMove(myPosition, newEndPosition, ChessPiece.PieceType.ROOK));
    moves.add(new ChessMove(myPosition, newEndPosition, ChessPiece.PieceType.KNIGHT));
  }

  /**
   * @return Which team this chess piece belongs to
   */
  public ChessGame.TeamColor getTeamColor() {
    return this.color;
  }

  /**
   * @return which type of chess piece this piece is
   */
  public PieceType getPieceType() {
    return this.type;
  }

  public void setPieceType(ChessPiece.PieceType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChessPiece that = (ChessPiece) o;
    return color == that.color && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(color, type);
  }

  private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
    var moves = new HashSet<ChessMove>();

    var thisColor = getTeamColor();
    var row = myPosition.getRow();
    var col = myPosition.getColumn();

    int[][] kingDirections = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    for (int[] direction : kingDirections) {
      int newRow = row + direction[0];
      int newCol = col + direction[1];

      if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
        continue;
      }

      var newEndPosition = new ChessPosition(newRow, newCol);
      var newMove = new ChessMove(myPosition, newEndPosition, null);

      var possibleCollision = board.hasPieceAt(newEndPosition);
      if (possibleCollision) {
        var collisionPiece = board.getPiece(newEndPosition);

        var isCollisionPieceEnemy = collisionPiece.getTeamColor() != thisColor;
        if (isCollisionPieceEnemy) {
          moves.add(newMove);
        }
      } else {
        moves.add(newMove);
      }
    }

    return moves;
  }

  private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
    var moves = new HashSet<ChessMove>();

    var thisColor = getTeamColor();
    var row = myPosition.getRow();
    var col = myPosition.getColumn();

    int[][] queenDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    for (int[] direction : queenDirections) {
      for (int distance = 1; distance <= 8; distance++) {
        int newQueenRow = row + direction[0] * distance;
        int newQueenCol = col + direction[1] * distance;

        if (newQueenRow < 1 || newQueenRow > 8 || newQueenCol < 1 || newQueenCol > 8) {
          break;
        }

        var newEndPosition = new ChessPosition(newQueenRow, newQueenCol);
        var newQueenMove = new ChessMove(myPosition, newEndPosition, null);

        var possibleCollision = board.hasPieceAt(newEndPosition);
        if (possibleCollision) {
          var collisionPiece = board.getPiece(newEndPosition);

          var isCollisionPieceEnemy = collisionPiece.getTeamColor() != thisColor;
          if (isCollisionPieceEnemy) {
            moves.add(newQueenMove);
          }

          break;
        } else {
          moves.add(newQueenMove);
        }
      }
    }

    return moves;
  }

  private HashSet<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
    var moves = new HashSet<ChessMove>();

    var thisColor = getTeamColor();
    var row = myPosition.getRow();
    var col = myPosition.getColumn();

    int[][] bishopDirections = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    for (int[] direction : bishopDirections) {
      for (int distance = 1; distance <= 8; distance++) {
        int newBishopRow = row + direction[0] * distance;
        int newBishopCol = col + direction[1] * distance;

        if (newBishopRow < 1 || newBishopRow > 8 || newBishopCol < 1 || newBishopCol > 8) {
          break;
        }

        var newEndPosition = new ChessPosition(newBishopRow, newBishopCol);
        var newBishopMove = new ChessMove(myPosition, newEndPosition, null);

        var possibleCollision = board.hasPieceAt(newEndPosition);
        if (possibleCollision) {
          var collisionPiece = board.getPiece(newEndPosition);

          var isCollisionPieceEnemy = collisionPiece.getTeamColor() != thisColor;
          if (isCollisionPieceEnemy) {
            moves.add(newBishopMove);
          }

          break;
        } else {
          moves.add(newBishopMove);
        }
      }
    }

    return moves;
  }

  private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
    var moves = new HashSet<ChessMove>();

    var thisColor = getTeamColor();
    var row = myPosition.getRow();
    var col = myPosition.getColumn();

    int[][] knightDirections = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

    for (int[] direction : knightDirections) {
      int newKnightRow = row + direction[0];
      int newKnightCol = col + direction[1];

      if (newKnightRow < 1 || newKnightRow > 8 || newKnightCol < 1 || newKnightCol > 8) {
        continue;
      }

      var newEndPosition = new ChessPosition(newKnightRow, newKnightCol);
      var newMove = new ChessMove(myPosition, newEndPosition, null);

      var possibleCollision = board.hasPieceAt(newEndPosition);
      if (possibleCollision) {
        var collisionPiece = board.getPiece(newEndPosition);

        var isCollisionPieceEnemy = collisionPiece.getTeamColor() != thisColor;
        if (isCollisionPieceEnemy) {
          moves.add(newMove);
        }
      } else {
        moves.add(newMove);
      }
    }

    return moves;
  }

  private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
    var moves = new HashSet<ChessMove>();

    var thisColor = getTeamColor();
    var row = myPosition.getRow();
    var col = myPosition.getColumn();

    int[][] rookDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] direction : rookDirections) {
      for (int distance = 1; distance <= 8; distance++) {
        int newRow = row + direction[0] * distance;
        int newCol = col + direction[1] * distance;

        if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
          break;
        }

        var newEndPosition = new ChessPosition(newRow, newCol);
        var newMove = new ChessMove(myPosition, newEndPosition, null);

        var possibleCollision = board.hasPieceAt(newEndPosition);
        if (possibleCollision) {
          var collisionPiece = board.getPiece(newEndPosition);

          var isCollisionPieceEnemy = collisionPiece.getTeamColor() != thisColor;
          if (isCollisionPieceEnemy) {
            moves.add(newMove);
          }

          break;
        } else {
          moves.add(newMove);
        }
      }
    }

    return moves;
  }

  private boolean isValidPosition(int col, int row) {
    return col >= 1 && col <= 8 && row >= 1 && row <= 8;
  }

  private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
    var moves = new HashSet<ChessMove>();

    var thisColor = getTeamColor();
    var row = myPosition.getRow();
    var col = myPosition.getColumn();

    // white: increments in row/col
    // black: decrements in row/col
    int direction = thisColor == ChessGame.TeamColor.WHITE ? 1 : -1;

    // note: promotion is mandatory, must check in order steps
    boolean isPromotionRow = (thisColor == ChessGame.TeamColor.WHITE && row == 7) || (thisColor == ChessGame.TeamColor.BLACK && row == 2);

    // forward movement (not conditional on first move -- can choose either)
    int newRow = row + direction;
    var newEndPosition = new ChessPosition(newRow, col);

    if (!isPromotionRow && newRow >= 1 && newRow <= 8 && !board.hasPieceAt(newEndPosition)) {
      var newMove = new ChessMove(myPosition, newEndPosition, null);

      moves.add(newMove);
    }

    // first move
    if ((thisColor == ChessGame.TeamColor.WHITE && row == 2) || (thisColor == ChessGame.TeamColor.BLACK && row == 7)) {
      newRow = row + 2 * direction;
      newEndPosition = new ChessPosition(newRow, col);

      // we need to make sure the middle square isn't occupied
      // because a pawn cannot jump over pieces
      var midRow = row + direction;
      var midPosition = new ChessPosition(midRow, col);

      if (col >= 1 && col <= 8 && !board.hasPieceAt(newEndPosition) && !board.hasPieceAt((midPosition))) {
        var newMove = new ChessMove(myPosition, newEndPosition, null);

        moves.add(newMove);
      }
    }

    // diagonal movement (capturing)
    newRow = row + direction;

    // first diagonal check
    int newCol = col + 1;

    newEndPosition = new ChessPosition(newRow, newCol);
    if (isValidPosition(newRow, newCol) && board.hasPieceAt(newEndPosition)) {
      var capturePiece = board.getPiece(newEndPosition);

      if (capturePiece.getTeamColor() != thisColor) {
        if (isPromotionRow) {
          pawnAddPromotionMoves(moves, myPosition, newEndPosition);
        } else {
          var newMove = new ChessMove(myPosition, newEndPosition, null);

          moves.add(newMove);
        }
      }
    }

    // second diagonal check
    newCol = col - 1;

    newEndPosition = new ChessPosition(newRow, newCol);
    if (isValidPosition(newRow, newCol) && board.hasPieceAt(newEndPosition)) {
      var capturePiece = board.getPiece(newEndPosition);

      if (capturePiece.getTeamColor() != thisColor) {
        if (isPromotionRow) {
          pawnAddPromotionMoves(moves, myPosition, newEndPosition);
        } else {
          var newMove = new ChessMove(myPosition, newEndPosition, null);

          moves.add(newMove);
        }
      }
    }

    // promotion
    newEndPosition = new ChessPosition(newRow, col);
    if (isPromotionRow && !board.hasPieceAt(newEndPosition)) {
      pawnAddPromotionMoves(moves, myPosition, newEndPosition);
    }

    return moves;
  }

  /**
   * Calculates all the positions a chess piece can move to
   * Do not take into account moves that are illegal due to leaving the king in
   * danger
   *
   * @return Collection of valid moves
   */
  public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
    return switch (getPieceType()) {
      case KING -> kingMoves(board, myPosition);
      case QUEEN -> queenMoves(board, myPosition);
      case BISHOP -> bishopMoves(board, myPosition);
      case KNIGHT -> knightMoves(board, myPosition);
      case ROOK -> rookMoves(board, myPosition);
      case PAWN -> pawnMoves(board, myPosition);
    };
  }

  /**
   * The various different chess piece options
   */
  public enum PieceType {
    KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
  }
}
