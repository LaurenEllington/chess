package service;

import dataaccess.*;
import resultrequest.ClearResult;
import server.DataAccessClasses;

public class ClearService {
    private DataAccessClasses daos;
    public ClearService(DataAccessClasses daos){
        this.daos=daos;
    }
    public ClearResult clear() throws ResponseException{
        try{
            daos.authDao().clearAuthData();
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        daos.gameDao().clearGameData();
        daos.userDao().clearUserData();
        return new ClearResult();
    }
}
