/**
 * Created by Panayiotis on 02/01/2016.
 */
public class Move {

    private Square from;
    private  Square to;
    private boolean isCapture;
    private boolean isEnPassantCapture;

    public Move(Square from, Square to, boolean isCapture, boolean isEnPassantCapture) {

        this.from               = new Square(from.getX(), from.getY());
        this.to                 = new Square(to.getX(), to.getY());
        this.isCapture          = isCapture;
        this.isEnPassantCapture = isEnPassantCapture;

    }

    public Move(Move move) {
        this.from               = move.getFrom();
        this.to                 = move.getTo();
        this.isCapture          = move.isCapture();
        this.isEnPassantCapture = move.isEnPassantCapture();
    }

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
        return to;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public boolean isEnPassantCapture() {
        return isEnPassantCapture;
    }

    public String getSAN() {

        String squareFrom = from.getSquareNotation();
        String squareTo   = to.getSquareNotation();

        if(!isCapture)
            return squareTo;
        else
            return squareFrom.charAt(0)+ "x" + squareTo;

    }

}
