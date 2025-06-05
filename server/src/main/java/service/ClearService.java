package service;

import resultrequest.ClearResult;
import server.DataAccessClasses;

public class ClearService {
    private DataAccessClasses daos;
    public ClearService(DataAccessClasses daos){
        this.daos=daos;
    }
    public ClearResult clear() throws ResponseException{
        daos.authDao().clearAuthData();
        daos.gameDao().clearGameData();
        daos.userDao().clearUserData();
        return new ClearResult();
    }
}
