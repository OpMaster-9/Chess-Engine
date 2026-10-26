package de.opmaster.chess;

import java.util.ArrayList;

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
        board[piece] ^= 1L << originSquare; //Remove piece at origin
        board[piece] |= 1L << destinationSquare; //Add piece at destination
    }

    //Direction Arrays
    private static final int[] KNIGHT_MOVES = {17, 15, 10, 6, -6, -10, -15, -17};
    private static final int[] BISHOP_DIRECTIONS = {9, 7, -7, -9};
    private static final int[] ROOK_DIRECTIONS = {8, 1, -1, -8};
    private static final int[] QUEEN_DIRECTIONS = {9, 8, 7 , 1, -1, -7 ,-8, -9};

    public ArrayList<Integer> getPossibleMoves(int position) {
        ArrayList<Integer> moves = new ArrayList<>(24);

        long empty = getEmpty();
        long blackPieces = getBlackPieces();
        long whitePieces = getWhitePieces();
        int file = position % 8;

         switch(getPieceAt(position)) {
             case WHITE_PAWN:
                 if ((empty & 1L << (position + 8)) != 0) { //1 forward
                     moves.add(position + 8);
                     if (position < 16 && position > 7 && (empty & 1L << (position + 16)) != 0) moves.add(position + 16); //2 forward
                 }

                 if (file > 0 &&(blackPieces & 1L << (position + 7)) != 0) moves.add(position + 7); //Capture left
                 if (file < 7 &&(blackPieces & 1L << (position + 9)) != 0) moves.add(position + 9); //Capture right
                 return moves;

             case WHITE_KNIGHT:
                 directMoves(moves, KNIGHT_MOVES, position, whitePieces);
                 return moves;

             case WHITE_BISHOP:
                 slidingMoves(moves, BISHOP_DIRECTIONS, position, empty, blackPieces);
                 return moves;

             case WHITE_ROOK:
                 slidingMoves(moves, ROOK_DIRECTIONS, position, empty, blackPieces);
                 return moves;

             case WHITE_QUEEN:
                 slidingMoves(moves, QUEEN_DIRECTIONS, position, empty, blackPieces);
                 return moves;

             case WHITE_KING:
                 directMoves(moves, QUEEN_DIRECTIONS, position, whitePieces);
                 return moves;

             case BLACK_PAWN:
                 if ((empty & 1L << (position - 8)) != 0) { //1 forward
                     moves.add(position - 8);
                     if (position < 56 && position > 47 && (empty & 1L << (position - 16)) != 0) moves.add(position - 16); //2 forward
                 }
                 if (file > 0 && (whitePieces & 1L << (position - 9)) != 0) moves.add(position - 9); //Capture left
                 if (file < 7 &&(whitePieces & 1L << (position - 7)) != 0) moves.add(position - 7); //Capture right
                 return moves;

             case BLACK_KNIGHT:
                 directMoves(moves, KNIGHT_MOVES, position, blackPieces);
                 return moves;

             case BLACK_BISHOP:
                 slidingMoves(moves, BISHOP_DIRECTIONS, position, empty, whitePieces);
                 return moves;

             case BLACK_ROOK:
                 slidingMoves(moves, ROOK_DIRECTIONS, position, empty, whitePieces);
                 return moves;

             case BLACK_QUEEN:
                 slidingMoves(moves, QUEEN_DIRECTIONS, position, empty, whitePieces);
                 return moves;

             case BLACK_KING:
                 directMoves(moves, QUEEN_DIRECTIONS, position, blackPieces);
                 return moves;

             default:
                 return moves;
         }
    }

    private void directMoves(ArrayList<Integer> moves, int[] directions, int position, long ownPieces) {
        int file = position % 8;
        for (int move : directions) {
            int target = move + position;

            if (target < 0 || target > 63) continue; //Prevent going outside board

            if (Math.abs(file - (target % 8)) > 2) continue; //Prevent wrapping around

            if ((ownPieces & 1L << target) == 0) moves.add(target); //Target is either an enemy piece or empty
        }
    }

    private void slidingMoves(ArrayList<Integer> moves, int[] directions, int position, long empty, long enemyPieces) {
        for(int direction : directions) {
            int current = position;

            while (true) {
                current = current + direction;

                if (current < 0 || current > 63) break; //Prevent going outside board

                if (Math.abs((current % 8) - ((current - direction) % 8)) > 1) break; //Prevent wrapping around

                if ((empty & (1L << current)) == 0) { //Stop on contact
                    if ((enemyPieces & 1L << current) != 0) moves.add(current); //Adding captures
                    break;
                }

                moves.add(current);
            }
        }
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