package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import chess.movecalculators.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves;
        switch(board.getPiece(myPosition).getPieceType()){
            case ROOK:
                RookMoveCalculator rookMoves = new RookMoveCalculator();
                moves = rookMoves.pieceMoves(board,myPosition);
                break;
            case BISHOP:
                BishopMoveCalculator bishopMoves = new BishopMoveCalculator();
                moves = bishopMoves.pieceMoves(board,myPosition);
                break;
            case KING:
                KingMoveCalculator kingMoves = new KingMoveCalculator();
                moves = kingMoves.pieceMoves(board,myPosition);
                break;
            case KNIGHT:
                KnightMoveCalculator knightMoves = new KnightMoveCalculator();
                moves = knightMoves.pieceMoves(board,myPosition);
                break;
            case PAWN:
                PawnMoveCalculator pawnMoves = new PawnMoveCalculator();
                moves = pawnMoves.pieceMoves(board,myPosition);
                break;
            case QUEEN:
                QueenMoveCalculator queenMoves = new QueenMoveCalculator();
                moves = queenMoves.pieceMoves(board,myPosition);
                break;
            default:
                moves = new ArrayList<ChessMove>();
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
