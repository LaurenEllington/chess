package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        int direction = (myColor == white) ? 1 : -1; //if white go positive direction if black go negative
        int row = myPosition.getRow() + direction;
        int col = myPosition.getColumn();
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {//if potential move location is in-bounds
            ChessPosition newPosition = new ChessPosition(row, col);
            ArrayList<ChessPosition> positions = new ArrayList<>();
            //if pawn can move forward add position to list
            if (board.getPiece(newPosition) == null){
                positions.add(newPosition);
                if ((myPosition.getRow()==2 && myColor==white) || (myPosition.getRow()==7 && myColor != white)){
                    if (board.getPiece(new ChessPosition(row+direction,col))==null){
                        positions.add(new ChessPosition(row+direction,col));
                    }
                }
            }
            //if pawn can capture right add position to list
            if (col+1 <= 8 && board.getPiece(new ChessPosition(row,col+1))!=null){
                if (board.getPiece(new ChessPosition(row,col+1)).getTeamColor()!=myColor){
                    positions.add(new ChessPosition(row,col+1));
                }
            }
            //if pawn can capture left add position to list
            if (col-1 >= 1 && board.getPiece(new ChessPosition(row,col-1))!=null){
                if (board.getPiece(new ChessPosition(row,col-1)).getTeamColor()!=myColor){
                    positions.add(new ChessPosition(row,col-1));
                }
            }
            for (ChessPosition position : positions) {
                if (position.getRow() == 8 || position.getRow() == 1) { //if piece is at the end promote
                    moves.add(new ChessMove(myPosition, position, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, position, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, position, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, position, ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, position, null));
                }
            }

        }


        return moves;
    }
}
