package chess.moveCalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        int direction = (myColor == white) ? 1 : -1; //if white go positive direction if black go negative
        int row = myPosition.getRow() + direction;
        int col = myPosition.getColumn();
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {//if potential move location is in-bounds
            ChessPosition newPosition = new ChessPosition(row, col); //try to move straight forward
            if (board.getPiece(newPosition) == null) {//if there is no piece in the potential move location
                if (row == 8 || row == 1){ //if piece is at the end promote
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                }
                else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                if ((myPosition.getRow() == 2 && myColor == white) || (myPosition.getRow() == 7 && myColor != white)) {//if the pawn is in the starting location for its color potentially move forward 2
                    newPosition = new ChessPosition(row + direction, col);
                    if (board.getPiece(newPosition) == null) {//if there is no piece in the potential move location
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }

            }
            if (col != 8){//capture on the right
                newPosition = new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn()+1);
                if (board.getPiece(newPosition)!=null && board.getPiece(newPosition).getTeamColor()!=myColor){ //if there is a piece to capture
                    if(row==8 || row == 1){ //if the pawn is at the end now
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    }
                    else { //pawn is not at the end
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
            if (col != 1){//capture on the left
                newPosition = new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn()-1);
                if (board.getPiece(newPosition)!=null && board.getPiece(newPosition).getTeamColor()!=myColor){ //if there is a piece to capture
                    if(row==8 || row == 1){ //if the pawn is at the end now promote
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    }
                    else { //pawn is not at the end
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }

        }


        return moves;
    }
}
