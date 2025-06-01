package dataaccess;

import model.AuthData;

public class MySqlAuthDao implements AuthDao{
    public void createAuth(AuthData a) throws DataAccessException{
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        return null;
    }
    public void deleteAuth(AuthData a) throws DataAccessException{
    }
    public void clearAuthData() throws DataAccessException{
    }
}
