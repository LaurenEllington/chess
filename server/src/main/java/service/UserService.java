package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import model.UserData;
import model.AuthData;
import resultrequest.*;

import java.util.ArrayList;


public class UserService {
    private MemoryUserDao userDao = new MemoryUserDao();
    private MemoryAuthDao authDao = new MemoryAuthDao();
    public RegisterResult register(RegisterRequest request) throws Exception{
        //verify that requested username, password, or email is not null or empty
        ArrayList<String> input = new ArrayList<String>();
        input.add(request.username());
        input.add(request.password());
        input.add(request.email());
        verifyInput(input,"username, password, or email");

        //verify that username isn't already taken
        if(userDao.getUser(request.username())!=null){
            throw new AlreadyTakenException("Username " + request.username() + " is already taken.");
        }

        //create userData object and add to database
        UserData user = new UserData(request.username(),request.password(),request.email());
        userDao.createUser(user);

        //log in the user
        AuthData auth = addAuthData(user.username());

        //create result
        return new RegisterResult(user.username(),auth.authToken());
    }
    public LoginResult login(LoginRequest request) throws Exception{
        //verify that requested username or password is not empty or null
        ArrayList<String> input = new ArrayList<String>();
        input.add(request.username());
        input.add(request.password());
        verifyInput(input,"username or password");

        //verify that user information is correct
        UserData user = userDao.getUser(request.username(),request.password());
        if(user==null){
            //make throw unauthorized exception
            throw new Exception("Unauthorized.");
        }

        //log in the user
        AuthData auth = addAuthData(user.username());

        //create result
        return new LoginResult(user.username(),auth.authToken());
    }
    public void logout(LogoutRequest request) throws Exception{
        //verify user identity
        AuthData authorization = authDao.getAuth(request.authToken());
        if(authorization==null){
            //make throw unauthorized exception
            throw new Exception("Unauthorized.");
        }

        authDao.deleteAuth(authorization);
    }


    private AuthData addAuthData(String username) throws Exception{
        String authToken = "";
        //figure out how to create a unique authToken?????
        AuthData auth = new AuthData(authToken,username);
        authDao.createAuth(auth);
        return auth;
    }
    //make throw bad request exception
    private void verifyInput(ArrayList<String> input, String inputType) throws Exception{
        for(String str : input){
            if(str==null||str.isEmpty()){
                throw new Exception("Invalid "+inputType+".");
            }
        }
    }
}
