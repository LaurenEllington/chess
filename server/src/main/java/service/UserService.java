package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import model.UserData;
import model.AuthData;
import resultrequest.*;

import java.util.ArrayList;
import java.util.UUID;


public class UserService {
    private MemoryUserDao userDao = new MemoryUserDao();
    private MemoryAuthDao authDao = new MemoryAuthDao();
    public RegisterResult register(RegisterRequest request) throws ResponseException{
        //verify that requested username, password, or email is not null or empty
        ArrayList<String> input = new ArrayList<String>();
        input.add(request.username());
        input.add(request.password());
        input.add(request.email());
        verifyInput(input);

        //verify that username isn't already taken
        UserData user;
        try{
            user = userDao.getUser(request.username());
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        if(user!=null){
            throw new ResponseException("Error: already taken",403);
        }

        //create userData object and add to database
        user = new UserData(request.username(),request.password(),request.email());
        try{
            userDao.createUser(user);
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }


        //log in the user
        AuthData auth = addAuthData(user.username());

        //create result
        return new RegisterResult(user.username(),auth.authToken());
    }
    public LoginResult login(LoginRequest request) throws ResponseException{
        //verify that requested username or password is not empty or null
        ArrayList<String> input = new ArrayList<String>();
        input.add(request.username());
        input.add(request.password());
        verifyInput(input);

        //verify that user information is correct
        UserData user;
        try {
            user = userDao.getUser(request.username(), request.password());
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        //if the user information is wrong
        if(user==null){
            //make throw unauthorized exception
            throw new ResponseException("Error: unauthorized", 401);
        }

        //log in the user
        AuthData auth = addAuthData(user.username());

        //create result
        return new LoginResult(user.username(),auth.authToken());
    }
    public LogoutResult logout(LogoutRequest request) throws ResponseException{
        //verify user identity
        AuthData authorization;
        try{
            authorization = authDao.getAuth(request.authToken());
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        if(authorization==null){
            //make throw unauthorized exception
            throw new ResponseException("Error: unauthorized",401);
        }
        try{
            authDao.deleteAuth(authorization);
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }

        return new LogoutResult();
    }


    private AuthData addAuthData(String username) throws ResponseException{
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken,username);
        try{
            authDao.createAuth(auth);
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }

        return auth;
    }
    //make throw bad request exception
    private void verifyInput(ArrayList<String> input) throws ResponseException{
        for(String str : input){
            if(str==null||str.isEmpty()){
                throw new ResponseException("Error: bad request",400);
            }
        }
    }
}
