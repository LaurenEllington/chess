package dataaccess;

import model.AuthData;

public interface AuthDao{
    void createAuth(AuthData a);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData a);
    void clearAuthData();
}
