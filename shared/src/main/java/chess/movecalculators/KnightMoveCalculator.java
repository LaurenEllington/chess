package chess.movecalculators;

import chess.*;

import java.util.Collection;

public class KnightMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] direction = {{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}};
        SingleMovement moves = new SingleMovement();
        return moves.pieceMoves(board,myPosition,direction);
    }
}
