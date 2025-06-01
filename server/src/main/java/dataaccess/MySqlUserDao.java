package dataaccess;

import model.UserData;

public class MySqlUserDao implements UserDao{
    public void createUser(UserData user) throws DataAccessException{

    }
    public UserData getUser(String username) throws DataAccessException{
        return null;
    }
    public UserData getUser(String username, String password) throws DataAccessException{
        return null;
    }
    public void clearUserData(){
    }
}
