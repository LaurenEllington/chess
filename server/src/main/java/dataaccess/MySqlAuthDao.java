package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySqlAuthDao implements AuthDao{
    public void createAuth(AuthData a) throws DataAccessException{
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        return null;
    }
    public void deleteAuth(AuthData a) throws DataAccessException{
    }
    public void clearAuthData() throws DataAccessException{
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }
    private AuthData executeUpdate(String statement, String... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement)){
                for(int i = 0; i<params.length;i++){
                    stmt.setString(i+1,params[i]);
                }
                ResultSet rs = stmt.executeQuery();
                if(rs.wasNull()){
                    return null;
                }
                return new AuthData(
                        rs.getString(1),rs.getString(2));
            }
        } catch (Exception e){
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()),500);
        }
    }
}
