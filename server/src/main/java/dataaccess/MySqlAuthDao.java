package dataaccess;

import model.AuthData;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySqlAuthDao implements AuthDao{
    public void createAuth(AuthData a){
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, false, a.authToken(), a.username());
    }
    public AuthData getAuth(String authToken) {
        var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
        return executeUpdate(statement, true, authToken);
    }
    public void deleteAuth(AuthData a){
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, false, a.authToken());
    }
    public void clearAuthData(){
        var statement = "TRUNCATE auth";
        executeUpdate(statement, false);
    }
    private AuthData executeUpdate(String statement, boolean query, String... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement)){
                for(int i = 0; i<params.length;i++){
                    stmt.setString(i+1,params[i]);
                }
                if (query) {
                    ResultSet rs = stmt.executeQuery();
                    if(rs.next()){
                        return new AuthData(
                                rs.getString(1),rs.getString(2));
                    }
                }
                else {
                    stmt.executeUpdate();
                }
                return null;
            }
        } catch (Exception e){
            throw new ResponseException(String.format("Error: unable to update database: %s, %s", statement, e.getMessage()),500);
        }
    }
}
