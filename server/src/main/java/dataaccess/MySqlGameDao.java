package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlGameDao implements GameDao{
    private int nextId=1;
    public void createGame(GameData game) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
            //autoincrement gameID and save into nextID
            executeUpdate(statement,nextId,game.whiteUsername(),game.blackUsername(),game.gameName(),game.game());
            //maybe turn chessgame into json (in update) if that will make it easier to revert back
        } catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public GameData getGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE game.gameID=?";
            try(PreparedStatement stmt = conn.prepareStatement(statement)){
                stmt.setInt(1, gameID);
                ResultSet rs = stmt.executeQuery();
                return new GameData(
                        rs.getInt(1),rs.getString(2),rs.getString(3),
                        rs.getString(4),new ChessGame());
            }           //new ChessGame/rs.getString(5) is a placeholder for however im supposed to make
                        //a string -> chessgame
        }
        catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public Collection<GameData> listGames() throws DataAccessException{
        return null;
    }
    public void updateGame(int gameID, GameData game) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE game set whiteUsername=?, blackUsername=?, gameName=?, chessGame=? where gameID=?";
            nextId = executeUpdate(statement,game.whiteUsername(),game.blackUsername(),game.gameName(),game.game(),gameID)+1;
        } catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clearGameData(){
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }
    public int getID(){
        return nextId;
    }
    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)){
                for(int i = 0; i<params.length;i++){
                    var param = params[i];
                    if(param instanceof String p) stmt.setString(i+1,p);
                    else if(param instanceof Integer p) stmt.setInt(i+1,p);
                    else if(param instanceof ChessGame.TeamColor p) stmt.setString(i+1,p.toString());
                    else if(param == null) stmt.setNull(i+1,NULL);
                }
                stmt.executeQuery();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()){
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (Exception e){
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()),500);
        }
    }
}
