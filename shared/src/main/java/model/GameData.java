package model;

import chess.ChessGame;

/*public class GameData { // white player color, black player color (usernames?), chessGame, gameID
}*/
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}
