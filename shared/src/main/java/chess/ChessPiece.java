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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();

        var thisType = getPieceType();
        var row = myPosition.getRow();
        var col = myPosition.getColumn();

        switch(thisType) {
            case KING:
            {
                break;
            }
            case QUEEN:
            {
                ;

                break;
            }
            case BISHOP:
            {
                // a bishop moves diagonally, so that's a possible four directions
                // let's calculate each direction individually

                // top left
                for (int i = row - 1, j = col - 1; i >= 1 && j >= 1; i--, j--) {
                    var newEndPosition = new ChessPosition(i, j);
                    var newMove = new ChessMove(myPosition, newEndPosition, null);

                    moves.add(newMove);
                }

                // top right
                for (int i = row - 1, j = col + 1; i >= 1 && j <= 8; i--, j++) {
                    var newEndPosition = new ChessPosition(i, j);
                    var newMove = new ChessMove(myPosition, newEndPosition, null);

                    moves.add(newMove);
                }

                // bottom left
                for (int i = row + 1, j = col - 1; i <= 8 && j >= 1; i++, j--) {
                    var newEndPosition = new ChessPosition(i, j);
                    var newMove = new ChessMove(myPosition, newEndPosition, null);

                    moves.add(newMove);
                }

                // bottom right
                for (int i = row + 1, j = col + 1; i <= 8 && j <= 8; i++, j++) {
                    var newEndPosition = new ChessPosition(i, j);
                    var newMove = new ChessMove(myPosition, newEndPosition, null);

                    moves.add(newMove);
                }

                break;
            }
            case KNIGHT:
            {
                ;
                ;
                ;

                break;
            }
            case ROOK:
            {
                ;
                ;
                ;
                ;

                break;
            }
            case PAWN:
            {
                ;
                ;
                ;
                ;
                ;

                break;
            }
            default:
            {
                throw new RuntimeException("[ChessPiece.pieceMoves] irregular piece tried to move!");
            }
        }

        return moves;
    }
}
