package dataaccess;

import model.UserData;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlUserDao implements UserDao{
    public void createUser(UserData user){
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String hash = encryptPassword(user.password());
        executeUpdate(statement, false, user.username(),hash,user.email());
    }
    public UserData getUser(String username){
        var statement = "SELECT username, password, email FROM user WHERE username=?";
        return executeUpdate(statement, true, username);
    }
    public UserData getUser(String username, String password){
        var user = getUser(username);
        if (user!=null && BCrypt.checkpw(password,user.password())){
            return user;
        }
        return null;
    }
    public void clearUserData(){
        var statement = "TRUNCATE user";
        executeUpdate(statement, false);
    }
    private String encryptPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }
    private UserData executeUpdate(String statement, boolean query, String... params) throws ResponseException {
        try(var conn = DatabaseManager.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setString(i + 1, params[i]);
                }
                if (query) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return new UserData(
                                rs.getString(1), rs.getString(2), rs.getString(3));
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
