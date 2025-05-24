package dataaccess;

import model.UserData;
import java.util.HashSet;

public class MemoryUserDao implements UserDao{
    private static HashSet<UserData> users = new HashSet<UserData>();
    public void createUser(UserData user) throws DataAccessException{
        users.add(user);
    }
    public UserData getUser(String username) throws DataAccessException{
        for(UserData user : users){
            if(user.username().equals(username)){
                return user;
            }
        }
        return null;
    }
    public UserData getUser(String username, String password) throws DataAccessException{
        for(UserData user : users){
            if(user.username().equals(username) && user.password().equals(password)){
                return user;
            }
        }
        return null;
    }
    public void deleteUser(String username) throws DataAccessException{
        users.remove(getUser(username));
    }
    public void clearUserData(){
        users.clear();
    }
}
