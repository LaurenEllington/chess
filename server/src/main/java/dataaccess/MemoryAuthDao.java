package dataaccess;

import model.AuthData;
import java.util.HashSet;

public class MemoryAuthDao implements AuthDao{
    private static HashSet<AuthData> auths = new HashSet<>();
    public void createAuth(AuthData a){
        auths.add(a);
    }
    public AuthData getAuth(String authToken){
        for(AuthData auth : auths){
            if(auth.authToken().equals(authToken)){
                return auth;
            }
        }
        return null;
    }
    public void deleteAuth(AuthData a){
        auths.remove(a);
    }
    public void clearAuthData(){
        auths.clear();
    }
}
