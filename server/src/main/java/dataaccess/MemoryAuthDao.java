package dataaccess;

import model.AuthData;
import java.util.HashSet;

public class MemoryAuthDao implements AuthDao{
    private static HashSet<AuthData> auths = new HashSet<AuthData>();
    public void createAuth(AuthData a) throws DataAccessException{
        auths.add(a);
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        for(AuthData auth : auths){
            if(auth.authToken().equals(authToken)){
                return auth;
            }
        }
        return null;
    }
    public void deleteAuth(AuthData a) throws DataAccessException{
        auths.remove(a);
    }
    public void clearAuthData() throws DataAccessException{
        auths.clear();
    }
}
