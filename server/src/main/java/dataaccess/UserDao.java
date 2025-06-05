package dataaccess;

import model.UserData;

public interface UserDao {
    void createUser(UserData user);
    UserData getUser(String username);
    UserData getUser(String username, String password);
    void clearUserData();
}
