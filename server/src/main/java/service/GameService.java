package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import model.AuthData;

public class GameService {
    private MemoryAuthDao authDao = new MemoryAuthDao();
    private MemoryGameDao gameDao = new MemoryGameDao();


    private AuthData authorize(String authToken) throws Exception{
        AuthData authorization = authDao.getAuth(authToken);
        if(authorization==null){
            //make throw unauthorized exception
            throw new Exception("Unauthorized.");
        }
        return authorization;
    }
}
