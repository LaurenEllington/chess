import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {
            new Server("sql").run(8080);
        } catch (Exception e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
    }
}