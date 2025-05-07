package chess.movecalculators;

import chess.*;

import java.util.Collection;

public class KingMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] direction = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,-1},{-1,1},{1,-1}};
        SingleMovement moves = new SingleMovement();
        return moves.pieceMoves(board,myPosition,direction);
    }
}
