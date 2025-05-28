package dataaccess;

import model.UserData;

public interface UserDao {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    UserData getUser(String username, String password) throws DataAccessException;
    void clearUserData();
}
