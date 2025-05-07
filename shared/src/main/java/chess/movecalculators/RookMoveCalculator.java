package chess.movecalculators;

import chess.*;


import java.util.Collection;
import java.util.ArrayList;

public class RookMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        int[][] direction = {{1,0},{-1,0},{0,1},{0,-1}};
        for(int i = 0; i < direction.length;i++){
            int rowModifier = direction[i][0];
            int colModifier = direction[i][1];
            int row = myPosition.getRow()+rowModifier;
            int col = myPosition.getColumn()+colModifier;
            while(row >= 1 && row <= 8 && col >=1 && col <= 8){
                ChessPosition newPosition = new ChessPosition(row,col);
                if(board.getPiece(newPosition)==null){//if there is no piece in the potential move location
                    moves.add(new ChessMove(myPosition,newPosition,null));
                }
                else{
                    if(board.getPiece(newPosition).getTeamColor()!=board.getPiece(myPosition).getTeamColor()){//if the piece is not of my color
                        moves.add(new ChessMove(myPosition,newPosition,null));//then i can potentially take it
                    }
                    break;
                }
                row+= rowModifier;
                col+= colModifier;
            }
        }

        return moves;
    }

}
