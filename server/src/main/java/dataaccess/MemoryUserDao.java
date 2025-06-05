package dataaccess;

import model.UserData;
import java.util.HashSet;

public class MemoryUserDao implements UserDao{
    private static HashSet<UserData> users = new HashSet<>();
    public void createUser(UserData user){
        users.add(user);
    }
    public UserData getUser(String username){
        for(UserData user : users){
            if(user.username().equals(username)){
                return user;
            }
        }
        return null;
    }
    public UserData getUser(String username, String password){
        for(UserData user : users){
            if(user.username().equals(username) && user.password().equals(password)){
                return user;
            }
        }
        return null;
    }
    public void clearUserData(){
        users.clear();
    }
}
