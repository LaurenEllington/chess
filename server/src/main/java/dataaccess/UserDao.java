package dataaccess;

import model.UserData;

public interface UserDao {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
    UserData getUser(String username, String password);
    void deleteUser(String username);
}
