package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import handler.JsonHandler;
import model.GameData;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlGameDao implements GameDao{
    public int createGame(GameData game) throws DataAccessException{
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement,game.whiteUsername(),game.blackUsername(),game.gameName(),game.game());
    }
    public GameData getGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE game.gameID=?";
            try(PreparedStatement stmt = conn.prepareStatement(statement)){
                stmt.setInt(1, gameID);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    return readGame(rs);
                }
                return null;
            }
        }
        catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public Collection<GameData> listGames() throws DataAccessException{
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        if(result.isEmpty()){
            return null;
        }
        return result;
    }
    public void updateGame(int gameID, GameData game){
        var statement = "UPDATE game set whiteUsername=?, blackUsername=?, gameName=?, chessGame=? where gameID=?";
        executeUpdate(statement,game.whiteUsername(),game.blackUsername(),game.gameName(),game.game(),gameID);
    }

    public void clearGameData(){
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }
    public int getID(){
        return 0;
    }
    private GameData readGame(ResultSet rs) throws SQLException{
        var game = new Gson().fromJson(rs.getString(5), ChessGame.class);
        return new GameData(
                rs.getInt(1),rs.getString(2),rs.getString(3),
                rs.getString(4), game);
    }
    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)){
                for(int i = 0; i<params.length;i++){
                    var param = params[i];
                    if(param instanceof String p) stmt.setString(i+1,p);
                    else if(param instanceof Integer p) stmt.setInt(i+1,p);
                    else if(param instanceof ChessGame p) stmt.setString(i+1, JsonHandler.serialize(p));
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
