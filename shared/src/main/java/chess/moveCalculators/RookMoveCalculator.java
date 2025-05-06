package chess.moveCalculators;

import chess.*;


import java.util.Collection;
import java.util.ArrayList;

public class RookMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        //i hate this code
        for(int col = myPosition.getColumn()-1;col>0;col--){//everything on same row as rook to the left
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(),col);
            if(board.getPiece(newPosition)==null){//if there is no piece in the potential move location
                moves.add(new ChessMove(myPosition,newPosition,null));
            }
            else{
                if(board.getPiece(newPosition).getTeamColor()!=board.getPiece(myPosition).getTeamColor()){//if the piece is not of my color
                    moves.add(new ChessMove(myPosition,newPosition,null));//then i can potentially take it
                }
                break;
            }
        }
        for(int col = myPosition.getColumn()+1;col<=8;col++){//everything on same row as rook to the right
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(),col);
            if(board.getPiece(newPosition)==null){//if there is no piece in the potential move location
                moves.add(new ChessMove(myPosition,newPosition,null));
            }
            else{
                if(board.getPiece(newPosition).getTeamColor()!=board.getPiece(myPosition).getTeamColor()){//if the piece is not of my color
                    moves.add(new ChessMove(myPosition,newPosition,null));//then i can potentially take it
                }
                break;
            }
        }
        //up/down
        for(int row = myPosition.getRow()-1;row>0;row--){//everything on same row as rook to the left
            ChessPosition newPosition = new ChessPosition(row,myPosition.getColumn());
            if(board.getPiece(newPosition)==null){//if there is no piece in the potential move location
                moves.add(new ChessMove(myPosition,newPosition,null));
            }
            else{
                if(board.getPiece(newPosition).getTeamColor()!=board.getPiece(myPosition).getTeamColor()){//if the piece is not of my color
                    moves.add(new ChessMove(myPosition,newPosition,null));//then i can potentially take it
                }
                break;
            }
        }
        for(int row = myPosition.getRow()+1;row<=8;row++){//everything on same row as rook to the right
            ChessPosition newPosition = new ChessPosition(row,myPosition.getColumn());
            if(board.getPiece(newPosition)==null){//if there is no piece in the potential move location
                moves.add(new ChessMove(myPosition,newPosition,null));
            }
            else{
                if(board.getPiece(newPosition).getTeamColor()!=board.getPiece(myPosition).getTeamColor()){//if the piece is not of my color
                    moves.add(new ChessMove(myPosition,newPosition,null));//then i can potentially take it
                }
                break;
            }
        }

        return moves;
    }

}
