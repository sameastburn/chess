package chess;

import java.util.Arrays;
import java.util.Map;
import java.util.Arrays;
import java.util.Objects;

class ArrayEqualityDebugger {

    // Method to compare two arrays
    public static <T> boolean compareArrays(T[] array1, T[] array2) {
        if (Arrays.equals(array1, array2)) {
            System.out.println("\narrays equal!");

            return true;
        } else {
            System.out.println("\narrays not equal: " + getDifferences(array1, array2));

            return false;
        }
    }

    // Method to find differences between two arrays
    private static <T> String getDifferences(T[] array1, T[] array2) {
        StringBuilder differences = new StringBuilder();
        int minLength = Math.min(array1.length, array2.length);

        // Check elements in the common length
        for (int i = 0; i < minLength; i++) {
            if (!Objects.deepEquals(array1[i], array2[i])) {
                differences.append(String.format("Difference at index %d: '%s' vs '%s'%n", i, array1[i], array2[i]));
            }
        }

        // Check if any array is longer than the other
        if (array1.length > minLength) {
            differences.append("Array1 has extra elements: ")
                    .append(Arrays.toString(Arrays.copyOfRange(array1, minLength, array1.length)))
                    .append("\n");
        }
        if (array2.length > minLength) {
            differences.append("Array2 has extra elements: ")
                    .append(Arrays.toString(Arrays.copyOfRange(array2, minLength, array2.length)))
                    .append("\n");
        }

        return differences.toString();
    }
}

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    // *** DEBUG *** CHANGE BACK TO PRIVATE
    public ChessPiece[][] board;

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
        var row = position.getRow();
        var column = position.getColumn();

        this.board[row][column] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        var row = position.getRow();
        var column = position.getColumn();

        return this.board[row][column];
    }

    final static Map<Character, ChessPiece.PieceType> charToTypeMap = Map.of(
            'p', ChessPiece.PieceType.PAWN,
            'n', ChessPiece.PieceType.KNIGHT,
            'r', ChessPiece.PieceType.ROOK,
            'q', ChessPiece.PieceType.QUEEN,
            'k', ChessPiece.PieceType.KING,
            'b', ChessPiece.PieceType.BISHOP);

    private static ChessBoard loadBoard(String boardText) {
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
                    ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;
                    var type = charToTypeMap.get(Character.toLowerCase(c));
                    var position = new ChessPosition(row, column);
                    var piece = new ChessPiece(color, type);
                    board.addPiece(position, piece);
                    column++;
                }
            }
        }
        return board;
    }

    @Override
    public boolean equals(Object o) {
        System.out.println("[ChessBoard.equals]");

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that=(ChessBoard) o;

        System.out.println("\n[ChessBoard.equals] ArrayEqualityDebugger.compareArrays");
        //boolean result = ArrayEqualityDebugger.compareArrays(board, that.board);

        //return result;//Arrays.equals(board, that.board);
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        var expectedBoard = loadBoard("""
                |r|n|b|q|k|b|n|r|
                |p|p|p|p|p|p|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                | | | | | | | | |
                |P|P|P|P|P|P|P|P|
                |R|N|B|Q|K|B|N|R|
                """);

        System.out.println("resetBoard");
        System.out.println(Arrays.toString(expectedBoard.board));

        // idk... maybe rewrite this, it's late
        this.board = expectedBoard.board;

        System.out.println("this.board");
        System.out.println(Arrays.toString(this.board));

        ;
    }
}
