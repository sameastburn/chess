package ui;

import chess.*;
import model.GameData;

import java.io.PrintStream;
import java.util.EnumMap;
import java.util.Set;
import java.util.stream.Collectors;

import static ui.EscapeSequences.CROWN;

public class UserInterface {
  private static final UserInterface instance = new UserInterface();
  private static final EnumMap<ChessPiece.PieceType, String> whitePiecesToUnicode = new EnumMap<>(ChessPiece.PieceType.class);
  private static final EnumMap<ChessPiece.PieceType, String> blackPiecesToUnicode = new EnumMap<>(ChessPiece.PieceType.class);
  // somehow making this static fixes weird race condition errors? ok
  public static volatile GameData gameData;
  public static ChessGame.TeamColor myTeamColor;
  public static boolean observing = false;
  private static ChessPosition highlightStartPosition;

  static {
    whitePiecesToUnicode.put(ChessPiece.PieceType.KING, EscapeSequences.WHITE_KING);
    whitePiecesToUnicode.put(ChessPiece.PieceType.QUEEN, EscapeSequences.WHITE_QUEEN);
    whitePiecesToUnicode.put(ChessPiece.PieceType.BISHOP, EscapeSequences.WHITE_BISHOP);
    whitePiecesToUnicode.put(ChessPiece.PieceType.KNIGHT, EscapeSequences.WHITE_KNIGHT);
    whitePiecesToUnicode.put(ChessPiece.PieceType.ROOK, EscapeSequences.WHITE_ROOK);
    whitePiecesToUnicode.put(ChessPiece.PieceType.PAWN, EscapeSequences.WHITE_PAWN);

    blackPiecesToUnicode.put(ChessPiece.PieceType.KING, EscapeSequences.BLACK_KING);
    blackPiecesToUnicode.put(ChessPiece.PieceType.QUEEN, EscapeSequences.BLACK_QUEEN);
    blackPiecesToUnicode.put(ChessPiece.PieceType.BISHOP, EscapeSequences.BLACK_BISHOP);
    blackPiecesToUnicode.put(ChessPiece.PieceType.KNIGHT, EscapeSequences.BLACK_KNIGHT);
    blackPiecesToUnicode.put(ChessPiece.PieceType.ROOK, EscapeSequences.BLACK_ROOK);
    blackPiecesToUnicode.put(ChessPiece.PieceType.PAWN, EscapeSequences.BLACK_PAWN);
  }

  private PrintStream out;

  public static UserInterface getInstance() {
    return instance;
  }

  public static String getUnicodeCharacter(ChessPiece.PieceType pieceType, ChessGame.TeamColor color) {
    return color == ChessGame.TeamColor.WHITE ? whitePiecesToUnicode.get(pieceType) : blackPiecesToUnicode.get(pieceType);
  }

  public void init(PrintStream outputStream) {
    out = outputStream;
  }

  public synchronized GameData getGameData() {
    return gameData;
  }

  public synchronized void setGameData(GameData gameDataArg) {
    gameData = gameDataArg;

    drawChessBoards();
  }

  public void printWelcomeHeader() {
    out.printf(CROWN + "Welcome to 240 chess. Type Help to get started." + CROWN + "%n");
  }

  public void printHelpPreLogin() {
    out.printf("\tregister <USERNAME> <PASSWORD> <EMAIL> - to create an account%n");
    out.printf("\tlogin <USERNAME> <PASSWORD> - to play chess%n");
    out.printf("\tquit - playing chess%n");
    out.printf("\thelp - with possible commands%n");
  }

  public void printHelpPostLogin() {
    out.printf("\tcreate <NAME> - a game%n");
    out.printf("\tlist - games%n");
    out.printf("\tjoin <ID> [WHITE|BLACK] - a game%n");
    out.printf("\tobserve <ID> - a game%n");
    out.printf("\tlogout - when you are done%n");
    out.printf("\tquit - playing chess%n");
    out.printf("\thelp - with possible commands%n");
  }

  public void drawChessBoard(ChessBoard chessBoard, ChessGame.TeamColor perspective) {
    drawChessBoard(chessBoard, perspective, Set.of());
  }

  public void drawChessBoard(ChessBoard chessBoard, ChessGame.TeamColor perspective, Set<ChessPosition> highlightPositions) {
    drawTopBottomBorder(perspective);

    boolean drawYellow = highlightPositions.size() > 0;

    for (int row = 1; row <= 8; row++) {
      int displayRow = perspective == ChessGame.TeamColor.WHITE ? 9 - row : row;
      System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + " " + displayRow + " " + EscapeSequences.RESET_BG_COLOR);

      for (int col = 1; col <= 8; col++) {
        int displayCol = perspective == ChessGame.TeamColor.WHITE ? col : 9 - col;

        ChessPiece piece = chessBoard.getPiece(new ChessPosition(displayRow, displayCol));
        String pieceCharacter = piece != null ? getUnicodeCharacter(piece.getPieceType(), piece.getTeamColor()) : EscapeSequences.EMPTY;

        var currentPosition = new ChessPosition(displayRow, displayCol);

        boolean shouldHighlight = highlightPositions.contains(currentPosition);
        String backgroundColor;

        if (shouldHighlight) {
          backgroundColor = EscapeSequences.SET_BG_COLOR_GREEN;
        } else if (drawYellow && currentPosition.equals(highlightStartPosition)) {
          backgroundColor = EscapeSequences.SET_BG_COLOR_YELLOW;
        } else {
          backgroundColor = (displayRow + col) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
        }

        System.out.print(backgroundColor + pieceCharacter + EscapeSequences.RESET_BG_COLOR);
      }

      System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + " " + displayRow + " " + EscapeSequences.RESET_BG_COLOR);
      System.out.println();
    }

    drawTopBottomBorder(perspective);
  }

  private void drawTopBottomBorder(ChessGame.TeamColor perspective) {
    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
    System.out.print("   ");

    char[] topOrBottomLetters = perspective == ChessGame.TeamColor.WHITE ? "abcdefgh".toCharArray() : "hgfedcba".toCharArray();
    for (char c : topOrBottomLetters) {
      System.out.print(" " + c + " ");
    }

    System.out.print("   ");
    System.out.print(EscapeSequences.RESET_BG_COLOR);
    System.out.println();
  }

  public void drawChessBoards() {
    System.out.println();

    if (observing) {
      drawChessBoard(gameData.game.getBoard(), ChessGame.TeamColor.BLACK);

      System.out.println();

      drawChessBoard(gameData.game.getBoard(), ChessGame.TeamColor.WHITE);
    } else {
      drawChessBoard(gameData.game.getBoard(), myTeamColor);
    }

    System.out.println();
  }

  public boolean highlightLegalMoves(ChessPosition startPosition) {
    var legalMoves = gameData.game.validMoves(startPosition);

    if (legalMoves.isEmpty()) {
      return false;
    }

    Set<ChessPosition> legalPositions = legalMoves.stream().map(ChessMove::getEndPosition).collect(Collectors.toSet());

    highlightStartPosition = startPosition;
    drawChessBoard(gameData.game.getBoard(), myTeamColor, legalPositions);

    return true;
  }

  public void printHelpInGame() {
    out.printf("\thelp - display all options%n");
    out.printf("\tredraw - redraws the chess board%n");
    out.printf("\tleave - leave the game%n");
    out.printf("\tmove - <PIECE> <POSITION> - move a piece at position to new position%n");
    out.printf("\tresign - forfeit%n");
    out.printf("\tmoves <POSITION> - display all visible moves%n");
  }
}
