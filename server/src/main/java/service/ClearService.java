package service;

import dataaccess.*;
import resultrequest.ClearResult;

public class ClearService {
    private MemoryAuthDao authDao = new MemoryAuthDao();
    private MemoryGameDao gameDao = new MemoryGameDao();
    private MemoryUserDao userDao = new MemoryUserDao();
    public ClearResult clear() throws Exception{
        authDao.clearAuthData();
        gameDao.clearGameData();
        userDao.clearUserData();
        return new ClearResult();
    }
}
