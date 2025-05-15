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
    //returns an altered board
    public ChessBoard potentialBoard(ChessMove move, ChessBoard board) {
        ChessBoard newBoard = board;
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(move.getPromotionPiece()!=null){
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        newBoard.addPiece(move.getStartPosition(),null);
        newBoard.addPiece(move.getEndPosition(),piece);
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
        ChessGame.TeamColor enemyColor = teamColor == ChessGame.TeamColor.WHITE? ChessGame.TeamColor.BLACK: ChessGame.TeamColor.WHITE;
        RuleBook rules = new RuleBook();
        Collection<ChessMove> enemyMoves = rules.allMoves(board,enemyColor);
        for(ChessMove move : enemyMoves){
            if(rules.wouldCaptureKing(move,board)){
                return true;
            }
        }
        return false;
    }
    public boolean isInCheckmate(ChessBoard board,ChessGame.TeamColor teamColor) {
        /*
        if(!isInCheck(board,teamColor)){
            return false;
        }
        Collection<ChessMove> allMoves = allMoves(board,teamColor);
        for(ChessMove move : allMoves){

        }
         */
        return false;
    }
    public boolean isInStalemate(ChessBoard board, ChessGame.TeamColor teamColor) {
        return false;
    }

    public Collection<ChessMove> validMoves(ChessBoard board,ChessPosition startPosition) {
        if(board.getPiece(startPosition)==null){
            return null;
        }
        RuleBook rules = new RuleBook();
        Collection<ChessMove> potentialMoves = board.getPiece(startPosition).pieceMoves(board,startPosition);
        potentialMoves.removeIf(move -> rules.wouldCaptureKing(move, board));
        //piece is not allowed to take King
        //make a potential board where the move is made and see if our team is in check
        for(ChessMove move : potentialMoves){
            ChessBoard potentialBoard = rules.potentialBoard(move,board);
            //if our team is put in check by this move
            if(rules.isInCheck(potentialBoard,board.getPiece(startPosition).getTeamColor())){
                potentialMoves.remove(move);
            }
        }
        return potentialMoves;
    }
}
