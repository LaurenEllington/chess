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
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)){
                setColorUsername(stmt,1,game.whiteUsername());
                setColorUsername(stmt,2,game.blackUsername());
                stmt.setString(3,game.gameName());
                stmt.setString(4,JsonHandler.serialize(game.game()));
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                keys.next();
                return keys.getInt(1);
            }
        } catch (Exception e){
            throw new DataAccessException(
                    String.format("unable to insert into database: %s, %s", statement, e.getMessage()));
        }
    }
    public GameData getGame(int gameID){
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?";
        return executeUpdate(statement, true, gameID);
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
        executeUpdate(statement, false, game.whiteUsername(),game.blackUsername(),game.gameName(),game.game(),gameID);
    }

    public void clearGameData(){
        var statement = "TRUNCATE game";
        executeUpdate(statement, false);
    }
    public int getID(){
        return 0;
    }
    private void setColorUsername(PreparedStatement stmt, int index, String colorUsername) throws SQLException{
        if (colorUsername == null) {
            stmt.setNull(index, NULL);
        } else {
            stmt.setString(index, colorUsername);
        }
    }
    //make sure that rs.getString on a string set to null gives the right value
    private GameData readGame(ResultSet rs) throws SQLException{
        var game = new Gson().fromJson(rs.getString(5), ChessGame.class);
        return new GameData(
                rs.getInt(1),rs.getString(2),rs.getString(3),
                rs.getString(4), game);
    }
    //works for update, clear, get
    private GameData executeUpdate(String statement, boolean query, Object... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement)){
                for(int i = 0; i<params.length;i++){
                    var param = params[i];
                    if(param instanceof String p) stmt.setString(i+1,p);
                    else if(param instanceof Integer p) stmt.setInt(i+1,p);
                    else if(param instanceof ChessGame p) stmt.setString(i+1, JsonHandler.serialize(p));
                    else if(param == null) stmt.setNull(i+1,NULL);
                }
                if (query){
                    ResultSet rs = stmt.executeQuery();
                    return readGame(rs);
                }
                else{
                    stmt.executeUpdate();
                }
                return null;
            }
        } catch (Exception e){
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()),500);
        }
    }
    /*
    private GameData executeUpdate(String statement, boolean query, Object... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)){
                for(int i = 0; i<params.length;i++){
                    var param = params[i];
                    if(param instanceof String p) stmt.setString(i+1,p);
                    else if(param instanceof Integer p) stmt.setInt(i+1,p);
                    else if(param instanceof ChessGame p) stmt.setString(i+1, JsonHandler.serialize(p));
                    else if(param == null) stmt.setNull(i+1,NULL);
                }
                if (query){
                    stmt.executeQuery();
                    ResultSet keys = stmt.getGeneratedKeys();
                    if (keys.next()){
                        return keys.getInt(1);
                    }
                }
                else{
                    stmt.executeUpdate();
                }
                return null;
            }
        } catch (Exception e){
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()),500);
        }
    }
     */
}
