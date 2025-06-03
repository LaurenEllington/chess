package dataaccess;

import model.UserData;
import service.ResponseException;

import java.sql.SQLException;

public class MySqlUserDao implements UserDao{
    public MySqlUserDao() throws DataAccessException{
        DatabaseManager.configureDatabase();
    }
    public void createUser(UserData user) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO user (name, password, email) VALUES (?, ?, ?)";
            executeUpdate(statement,user.username(),user.password(),user.email());
        } catch (Exception e){
            throw new DataAccessException(e.getMessage());
        }

    }
    public UserData getUser(String username) throws DataAccessException{
        return null;
    }
    public UserData getUser(String username, String password) throws DataAccessException{
        return null;
    }
    public void clearUserData(){
    }
    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){

        } catch (Exception e){
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()),500);
        }
    }
}
