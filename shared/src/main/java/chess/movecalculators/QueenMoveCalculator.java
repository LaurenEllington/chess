package chess.movecalculators;

import chess.*;

import java.util.Collection;

public class QueenMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] direction = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{-1,1},{1,-1}};
        DirectionMovement moves = new DirectionMovement();
        return moves.pieceMoves(board,myPosition,direction);
    }
}
