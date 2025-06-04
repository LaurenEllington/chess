package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlGameDao implements GameDao{
    public void createGame(GameData game) throws DataAccessException{}
    public GameData getGame(int gameID) throws DataAccessException{
        return null;
    }
    public Collection<GameData> listGames() throws DataAccessException{
        return null;
    }
    public void updateGame(int gameID, GameData game) throws DataAccessException{}
    public void clearGameData(){
        var statement = "TRUNCATE game";
        executeUpdate(statement);
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
