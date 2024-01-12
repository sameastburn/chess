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
    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
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

        // took an iterative approach this time because this function would have been really long
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                var newEndPosition = new ChessPosition(newRow, newCol);
                var newMove = new ChessMove(myPosition, newEndPosition, null);

                var possibleCollision = board.hasPieceAt(newEndPosition);
                if (possibleCollision) {
                    var collisionPiece = board.getPiece(newEndPosition);

                    // if we collide with another piece, if it's an enemy it's a possible move
                    // however if it's one of our own pieces, we cannot capture our own pieces
                    if (collisionPiece.getTeamColor() != thisColor) {
                        moves.add(newMove);
                    }
                } else {
                    moves.add(newMove);
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();

        var thisColor = getTeamColor();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] direction : directions) {
            for (int distance = 1; distance <= 8; distance++) {
                int newRow= row + direction[0] * distance;
                int newCol= col + direction[1] * distance;

                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    var newEndPosition = new ChessPosition(newRow, newCol);
                    var newMove = new ChessMove(myPosition, newEndPosition, null);

                    var possibleCollision = board.hasPieceAt(newEndPosition);
                    if (possibleCollision) {
                        var collisionPiece = board.getPiece(newEndPosition);

                        // if we collide with another piece, if it's an enemy it's a possible move
                        // however if it's one of our own pieces, we cannot capture our own pieces
                        if (collisionPiece.getTeamColor() != thisColor) {
                            moves.add(newMove);
                        }

                        break;
                    } else {
                        moves.add(newMove);
                    }
                } else {
                    // because we are looping max distance [1-8], no need to waste iterations

                    break;
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

        // a bishop moves diagonally, so that's a possible four directions
        // let's calculate each direction individually

        // bottom left
        for (int i = row - 1, j = col - 1; i >= 1 && j >= 1; i--, j--) {
            var newEndPosition = new ChessPosition(i, j);
            var newMove = new ChessMove(myPosition, newEndPosition, null);

            var possibleCollision = board.hasPieceAt(newEndPosition);
            if (possibleCollision) {
                var collisionPiece = board.getPiece(newEndPosition);

                // if we collide with another piece, if it's an enemy it's a possible move
                // however if it's one of our own pieces, we cannot capture our own pieces
                if (collisionPiece.getTeamColor() != thisColor) {
                    moves.add(newMove);
                }

                break;
            }

            moves.add(newMove);
        }

        // bottom right
        for (int i = row - 1, j = col + 1; i >= 1 && j <= 8; i--, j++) {
            var newEndPosition = new ChessPosition(i, j);
            var newMove = new ChessMove(myPosition, newEndPosition, null);

            var possibleCollision = board.hasPieceAt(newEndPosition);
            if (possibleCollision) {
                var collisionPiece = board.getPiece(newEndPosition);
                if (collisionPiece.getTeamColor() != thisColor) {
                    moves.add(newMove);
                }

                break;
            }

            moves.add(newMove);
        }

        // top left
        for (int i = row + 1, j = col - 1; i <= 8 && j >= 1; i++, j--) {
            var newEndPosition = new ChessPosition(i, j);
            var newMove = new ChessMove(myPosition, newEndPosition, null);

            var possibleCollision = board.hasPieceAt(newEndPosition);
            if (possibleCollision) {
                var collisionPiece = board.getPiece(newEndPosition);
                if (collisionPiece.getTeamColor() != thisColor) {
                    moves.add(newMove);
                }

                break;
            }

            moves.add(newMove);
        }

        // top right
        for (int i = row + 1, j = col + 1; i <= 8 && j <= 8; i++, j++) {
            var newEndPosition = new ChessPosition(i, j);
            var newMove = new ChessMove(myPosition, newEndPosition, null);

            var possibleCollision = board.hasPieceAt(newEndPosition);
            if (possibleCollision) {
                var collisionPiece = board.getPiece(newEndPosition);
                if (collisionPiece.getTeamColor() != thisColor) {
                    moves.add(newMove);
                }

                break;
            }

            moves.add(newMove);
        }

        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();

        var thisColor = getTeamColor();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        int[][] directions = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                var newEndPosition = new ChessPosition(newRow, newCol);
                var newMove = new ChessMove(myPosition, newEndPosition, null);

                var possibleCollision = board.hasPieceAt(newEndPosition);
                if (possibleCollision) {
                    var collisionPiece = board.getPiece(newEndPosition);

                    // if we collide with another piece, if it's an enemy it's a possible move
                    // however if it's one of our own pieces, we cannot capture our own pieces
                    if (collisionPiece.getTeamColor() != thisColor) {
                        moves.add(newMove);
                    }
                } else {
                    moves.add(newMove);
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();

        var thisColor = getTeamColor();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            for (int distance = 1; distance <= 8; distance++) {
                int newRow= row + direction[0] * distance;
                int newCol= col + direction[1] * distance;

                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    var newEndPosition = new ChessPosition(newRow, newCol);
                    var newMove = new ChessMove(myPosition, newEndPosition, null);

                    var possibleCollision = board.hasPieceAt(newEndPosition);
                    if (possibleCollision) {
                        var collisionPiece = board.getPiece(newEndPosition);

                        // if we collide with another piece, if it's an enemy it's a possible move
                        // however if it's one of our own pieces, we cannot capture our own pieces
                        if (collisionPiece.getTeamColor() != thisColor) {
                            moves.add(newMove);
                        }

                        break;
                    } else {
                        moves.add(newMove);
                    }
                } else {
                    // because we are looping max distance [1-8], no need to waste iterations

                    break;
                }
            }
        }

        return moves;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch(getPieceType()) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case PAWN -> null;
            default -> {
                throw new RuntimeException("[ChessPiece.pieceMoves] irregular piece tried to move!");
            }
        };
    }
}
