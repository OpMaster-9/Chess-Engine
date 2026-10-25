package de.opmaster.chess;



public class Board {
    //Indexes for white Pieces
    private final int WHITE_PAWN = 0;
    private final int WHITE_KNIGHT = 1;
    private final int WHITE_BISHOP = 2;
    private final int WHITE_ROOK = 3;
    private final int WHITE_QUEEN = 4;
    private final int WHITE_KING = 5;

    //Indexes for black pieces
    private final int BLACK_PAWN = 6;
    private final int BLACK_KNIGHT = 7;
    private final int BLACK_BISHOP = 8;
    private final int BLACK_ROOK = 9;
    private final int BLACK_QUEEN = 10;
    private final int BLACK_KING = 11;

    //Board
    private long[] board = {0x000000000000FF00L, 0x0000000000000042L, 0x0000000000000024L, 0x0000000000000081L, 0x0000000000000008L, 0x0000000000000010L, 0x00FF000000000000L, 0x4200000000000000L, 0x2400000000000000L, 0x8100000000000000L, 0x0800000000000000L, 0x1000000000000000L};

    //Game variables
    private boolean whiteToMove = true;
    private int availableCastles = 15; //Stores available Castling moves as 4 bits (Black Queenside(8), Black Kingside(4), White Queenside(2), White Kingside(1))
    private int enPassantSquare = -1;
    private int halfMoves = 0; //Counts half Moves since last capture for 50 move draw rule
    private int fullMoves = 1; //Counts up only after Black moved

    //Movement Functions
    public void movePiece(int originSquare, int destinationSquare) {
        int piece = getPieceAt(originSquare);
        board[piece] ^= 1l << originSquare;
        board[piece] |= 1L << destinationSquare;
    }




    //Get Functions
    public long getWhitePieces() {
        return board[WHITE_PAWN] | board[WHITE_KNIGHT] | board[WHITE_BISHOP] | board[WHITE_ROOK] | board[WHITE_QUEEN] | board[WHITE_KING];
    }

    public long getBlackPieces() {
        return board[BLACK_PAWN] | board[BLACK_KNIGHT] | board[BLACK_BISHOP] | board[BLACK_ROOK] | board[BLACK_QUEEN] | board[BLACK_KING];
    }

    public long getOccupied() {
        return getWhitePieces() | getBlackPieces();
    }

    public long getEmpty() {
        return ~getOccupied();
    }

    public int getPieceAt(int square) {
        long mask = 1L << square;

        for (int i = 0; i < board.length; i++) {
            if ((board[i] & mask) != 0) return i;
        }

        return 12;
    }

    //Output Functions
    public void printBoard() {
        char[] abc = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        for (int i = 7; i >= 0; i--) {
            System.out.print(abc[i] + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(getCharAt(i * 8 + j) + " ");
            }
            System.out.println();
        }
        System.out.println("  1 2 3 4 5 6 7 8");
    }

    public char getCharAt(int square) {
        long mask = 1L << square;

        if ((board[WHITE_PAWN] & mask) != 0)   return 'P';
        if ((board[WHITE_KNIGHT] & mask) != 0) return 'N';
        if ((board[WHITE_BISHOP] & mask) != 0) return 'B';
        if ((board[WHITE_ROOK] & mask) != 0)   return 'R';
        if ((board[WHITE_QUEEN] & mask) != 0)  return 'Q';
        if ((board[WHITE_KING] & mask) != 0)   return 'K';
        if ((board[BLACK_PAWN] & mask) != 0)   return 'p';
        if ((board[BLACK_KNIGHT] & mask) != 0) return 'n';
        if ((board[BLACK_BISHOP] & mask) != 0) return 'b';
        if ((board[BLACK_ROOK] & mask) != 0)   return 'r';
        if ((board[BLACK_QUEEN] & mask) != 0)  return 'q';
        if ((board[BLACK_KING] & mask) != 0)   return 'k';

        return '.';
    }
}