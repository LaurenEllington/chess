package server;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;

public record DataAccessClasses(UserDao userDao, GameDao gameDao, AuthDao authDao) {
}
