package dataaccess;

import model.UserData;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlUserDao implements UserDao{
    public MySqlUserDao() throws DataAccessException{
        DatabaseManager.configureDatabase();
    }
    public void createUser(UserData user) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            executeUpdate(statement,user.username(),user.password(),user.email());
        } catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }

    }
    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM user WHERE user.username=?";
            return executeUpdate(statement,username);
        }
        catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public UserData getUser(String username, String password) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM user WHERE user.username=? AND user.password=?";
            return executeUpdate(statement,username,password);
        }
        catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
    public void clearUserData(){
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }
    private UserData executeUpdate(String statement, String... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement)){
                for(int i = 0; i<params.length;i++){
                    stmt.setString(i+1,params[i]);
                }
                ResultSet rs = stmt.executeQuery();
                if(rs.wasNull()){
                    return null;
                }
                return new UserData(
                        rs.getString(1),rs.getString(2),rs.getString(3));
            }
        } catch (Exception e){
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()),500);
        }
    }
}
