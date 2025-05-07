package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] direction = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{-1,1},{1,-1}};
        directionMovement moves = new directionMovement();
        return moves.pieceMoves(board,myPosition,direction);
    }
}
