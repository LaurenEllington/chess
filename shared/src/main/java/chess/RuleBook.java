package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RuleBook {
    //returns all moves a color can make regardless of whether they are legal
    public Collection<ChessMove> allMoves(ChessBoard board, ChessGame.TeamColor myColor) {
        //needs to be given an altered chessboard with the potential move and see if the king is now takeable
        //for check & valid moves
        ArrayList<ChessMove> moves = new ArrayList<>();
        for(int row = 1; row <=8; row++){
            for(int col = 1; col<=8; col++){
                ChessPiece currentPiece = board.getPiece(new ChessPosition(row,col));
                if(currentPiece!=null && currentPiece.getTeamColor()==myColor){
                    moves.addAll(currentPiece.pieceMoves(board,new ChessPosition(row,col)));
                }
            }
        }
        return moves;
    }
    //returns all valid moves a color can make
    public Collection<ChessMove> allValidMoves(ChessBoard board, ChessGame.TeamColor myColor) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for(int row = 1; row <=8; row++){
            for(int col = 1; col<=8; col++){
                ChessPiece currentPiece = board.getPiece(new ChessPosition(row,col));
                if(currentPiece!=null && currentPiece.getTeamColor()==myColor){
                    moves.addAll(validMoves(board,new ChessPosition(row,col)));
                }
            }
        }
        return moves;
    }
    //returns an altered board
    public ChessBoard potentialBoard(ChessMove move, ChessBoard board) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(move.getPromotionPiece()!=null){
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        ChessBoard newBoard = new ChessBoard(board);
        newBoard.addPiece(move.getEndPosition(),piece);
        newBoard.addPiece(move.getStartPosition(),null);
        return newBoard;
    }
    //returns true if move would capture a king
    //because of the way piecemoves is structured, friendly fire is not allowed
    //so true if move captures enemy king
    public boolean wouldCaptureKing(ChessMove move, ChessBoard board) {
        if(board.getPiece(move.getEndPosition())==null){ //if there is no piece to capture
            return false;
        }
        return board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING;
    }

    public boolean isInCheck(ChessBoard board, ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor enemyColor;
        if(teamColor==ChessGame.TeamColor.WHITE){
            enemyColor=ChessGame.TeamColor.BLACK;
        }
        else{
            enemyColor=ChessGame.TeamColor.WHITE;
        }
        Collection<ChessMove> enemyMoves = allMoves(board,enemyColor);
        for(ChessMove move : enemyMoves){
            if(wouldCaptureKing(move,board)){
                return true;
            }
        }
        return false;
    }
    public boolean isInCheckmate(ChessBoard board,ChessGame.TeamColor teamColor,ChessGame.TeamColor teamTurn) {
        if(!isInCheck(board,teamColor)||teamColor!=teamTurn){
            return false;
        }
        Collection<ChessMove> allValidMoves = allValidMoves(board,teamColor);
        return allValidMoves.isEmpty();
    }
    public boolean isInStalemate(ChessBoard board, ChessGame.TeamColor teamColor, ChessGame.TeamColor teamTurn) {
        if(isInCheck(board,teamColor) || teamColor!=teamTurn){
            return false;
        }
        Collection<ChessMove> allValidMoves = allValidMoves(board,teamColor);
        return allValidMoves.isEmpty();
    }

    public Collection<ChessMove> validMoves( ChessBoard board,ChessPosition startPosition) {
        if(board.getPiece(startPosition)==null){
            return null;
        }
        ChessGame.TeamColor myColor = board.getPiece(startPosition).getTeamColor();
        ArrayList<ChessMove> potentialMoves = new ArrayList<>(board.getPiece(startPosition).pieceMoves(board,startPosition));
        //make a potential board where the move is made and see if our team is in check
        for(int i = 0; i < potentialMoves.size(); i++){
            ChessBoard potential = potentialBoard(potentialMoves.get(i),board);
            //if our team is put in check by this move
            if(isInCheck(potential,myColor)){
                potentialMoves.remove(potentialMoves.get(i));
                i--;
            }
        }
        return potentialMoves;
    }
}
