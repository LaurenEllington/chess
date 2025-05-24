package service;

import dataaccess.*;

public class ClearService {
    private MemoryAuthDao authDao = new MemoryAuthDao();
    private MemoryGameDao gameDao = new MemoryGameDao();
    private MemoryUserDao userDao = new MemoryUserDao();
    void clear() throws Exception{
        authDao.clearAuthData();
        gameDao.clearGameData();
        userDao.clearUserData();
    }
}
