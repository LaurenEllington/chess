package dataaccess;

import model.AuthData;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDao implements AuthDao{
    public void createAuth(AuthData a) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            executeUpdate(statement, a.authToken(), a.username());
        } catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
            return executeUpdate(statement,authToken);
        }
        catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public void deleteAuth(AuthData a) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth WHERE authToken=?";
            executeUpdate(statement, a.authToken());
        } catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public void clearAuthData() throws DataAccessException{
        var statement = "TRUNCATE auth";
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
