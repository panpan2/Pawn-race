/**
 * Created by Panayiotis on 03/01/2016.
 */
public class GameUtil {
    //utility class for Game class

    public static Color oppositePlayer(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public static boolean blackIsOutOfPawns(Board board) {

        for(int file = 0; file < 8; file++) {
            for(int rank = 0; rank < 8; rank++){
                if(board.getSquare(file, rank).occupiedBy() == Color.BLACK)
                    return false;
            }
        }
        return true;

    }

    public static boolean whiteIsOutOfPawns(Board board) {

        for(int file = 0; file < 8; file++) {
            for(int rank = 0; rank < 8; rank++){
                if(board.getSquare(file, rank).occupiedBy() == Color.WHITE)
                    return false;
            }
        }
        return true;

    }

    public static boolean pawnOnLastRank(Board board, Color playerColor) {

        for(int file = 0; file < 8; file++) {
            if(board.getSquare(file, 0).occupiedBy() == playerColor)
                return true;
            if(board.getSquare(file, 7).occupiedBy() == playerColor)
                return true;
        }
        return false;

    }

    public static boolean stealMate(Board board, Color currentPlayer, Move lastMove) {

        for(int file = 0; file < 8; file++) {
            for(int rank = 0; rank < 8; rank++){
                if(board.getSquare(file, rank).occupiedBy() == currentPlayer) {
                    if(canMove(board, file, rank, lastMove))
                        return false;
                }
            }
        }
        //no possible moves found, checked all pawns
        return true;
    }

    private static boolean canMove(Board board, int file, int rank, Move lastMove) {

        if(forwardMove(board, file, rank))
            return true;
        if(captureMove(board, file, rank))
            return true;
        if(enPassantCaptureMove(board, file, rank, lastMove))
            return true;

        return false;
    }

    private static boolean forwardMove(Board board, int file, int rank) {
        Color pawnColor = board.getSquare(file, rank).occupiedBy();
        int direction = getDirectionOfPawn(pawnColor);
        if(board.getSquare(file, rank + direction).occupiedBy() == Color.NONE)
            return true;
        return false;
    }
    private static boolean captureMove(Board board, int file, int rank) {
        Color pawnColor = board.getSquare(file, rank).occupiedBy();
        int direction = getDirectionOfPawn(pawnColor);

        if(file > 0 && board.getSquare(file - 1, rank + direction).occupiedBy() == oppositePlayer(pawnColor))
            return true;
        
        if(file < 7 && board.getSquare(file + 1, rank + direction).occupiedBy() == oppositePlayer(pawnColor))
            return true;
        
        return false;
    }

    private static boolean enPassantCaptureMove(Board board, int file, int rank, Move lastMove) {
        Color pawnColor = board.getSquare(file, rank).occupiedBy();
        int direction = getDirectionOfPawn(pawnColor);

        if(!lastMove.isEnPassantCapture())
            return false;

        int enPassantPawnX = lastMove.getTo().getX();
        int enPassantPawnY = lastMove.getTo().getY();
        int toMovePawnX = board.getSquare(file, rank).getX();
        int toMovePawnY = board.getSquare(file, rank).getY();

        if(enPassantPawnY == toMovePawnY &&
                (enPassantPawnX == toMovePawnX + 1 || enPassantPawnX == toMovePawnX - 1))
            return true;

        return false;
    }

    private static int getDirectionOfPawn(Color pawn) {
        switch(pawn) {
            case WHITE: return 1;
            case BLACK: return -1;
            default: assert(true): "Pawn should be WHITE/BLACK"; return 0;
        }
    }

    public static boolean validMove(Square from, Square to, boolean isCapture,
                                    boolean isEnPassantCapture, Board board, Color player,
                                    Move lastMove) {

        if(from.occupiedBy() != player || to.occupiedBy() == player)
            return false;

        int direction = getDirectionOfPawn(player); // +1 for White, -1 for Black
        if(isEnPassantCapture) {

            int fileDifference = to.getX() - from.getX();
            if(to.getY() - direction < 0) return false;
            Square lastFrom = lastMove.getFrom();
            Square lastTo = lastMove.getTo();

            Square enPassantPawn = board.getSquare(to.getX(), to.getY() - direction);
            if(to.occupiedBy() == Color.NONE &&
                    (to.getY() == from.getY() + direction) &&
                    (fileDifference == 1 || fileDifference == -1) &&
                    (enPassantPawn.occupiedBy() == oppositePlayer(player)) &&
                    (lastTo.getY() - lastFrom.getY() == 2) &&
                    (lastTo.getX() == enPassantPawn.getX()) &&
                    (lastTo.getY() == enPassantPawn.getY()))//that was the pawn moved
                return true;
            else
                return false;

        } else if(isCapture) {
            if(to.occupiedBy() == oppositePlayer(player) &&
                    (from.getX() + 1 == to.getX() || from.getX() - 1 == to.getX()) &&
                    (from.getY() + direction == to.getY()))
                return true;
            return false;
        } else {
            if((from.getY() == to.getY() - 1 || from.getY() == to.getY() - 2) &&
                    (from.getX() == to.getX()) && to.occupiedBy() == Color.NONE &&
                    (board.getSquare(from.getX(), from.getY() + 1).occupiedBy() == Color.NONE))
                return true;
            return false;
        }
    }

    public static Move stringToMove(String san, Move lastMove, int nextMoveIndex, Board board, Color player) {

        if(san.length() != 5) //weak check, if notation is changed later must change too 
            return null;

        String squareString;
        squareString = san.substring(0, 2);
        Square from = notationToSquare(squareString, board);
        squareString = san.substring(3, 5);
        Square to = notationToSquare(squareString, board);
        boolean isCapture = san.contains("x");
        boolean isEnPassantCapture = isCapture && (to.occupiedBy() == Color.NONE);
        
        //check if it's valid

        //extra care, in this case lastMove is null
        if(nextMoveIndex == 0) {
            if(from.occupiedBy() == player && to.getX() == from.getX()
                    && (to.getY() == from.getY() + 1 || to.getY() == from.getY() + 2)) {
                Move move = new Move(from, to, isCapture, isEnPassantCapture);
                return move;
            }
            //invalid move
            return null;
        }

        //normal move, able to use lastMove
        if(validMove(from, to, isCapture, isEnPassantCapture, board, player, lastMove)) {
            Move move = new Move(from, to, isCapture, isEnPassantCapture);
            return move;
        }

        return null;
    }
    private static Square notationToSquare(String squareString, Board board) {
        assert(squareString.length() == 2):
                "squareString should only have 2 characters";
        int file = squareString.charAt(0) - 'A';
        int rank = squareString.charAt(1) - '0';
        //reference to the board square
        Square square = board.getSquare(file, rank);
        return square;
    }
}
