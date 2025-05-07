package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class SingleMovement {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, int[][] direction) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int[] ints : direction) {
            int rowModifier = ints[0];
            int colModifier = ints[1];
            int row = myPosition.getRow() + rowModifier;
            int col = myPosition.getColumn() + colModifier;
            if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (board.getPiece(newPosition) == null) {//if there is no piece in the potential move location
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {//if the piece is not of my color
                        moves.add(new ChessMove(myPosition, newPosition, null));//then i can potentially take it
                    }
                }
            }
        }

        return moves;
    }
}
