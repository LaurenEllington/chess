package service;

import model.UserData;
import model.AuthData;
import resultrequest.*;
import server.DataAccessClasses;

import java.util.ArrayList;
import java.util.UUID;


public class UserService {
    private DataAccessClasses daos;
    public UserService(DataAccessClasses daos){
        this.daos=daos;
    }
    public RegisterResult register(RegisterRequest request) throws ResponseException{
        //verify that requested username, password, or email is not null or empty
        ArrayList<String> input = new ArrayList<String>();
        input.add(request.username());
        input.add(request.password());
        input.add(request.email());
        verifyInput(input);

        //verify that username isn't already taken
        var user = daos.userDao().getUser(request.username());
        if(user!=null){
            throw new ResponseException("Error: already taken",403);
        }

        //create userData object and add to database
        user = new UserData(request.username(),request.password(),request.email());
        daos.userDao().createUser(user);


        //log in the user
        AuthData auth = addAuthData(user.username());

        //create result
        return new RegisterResult(user.username(),auth.authToken());
    }
    public LoginResult login(LoginRequest request) throws ResponseException{
        //verify that requested username or password is not empty or null
        ArrayList<String> input = new ArrayList<>();
        input.add(request.username());
        input.add(request.password());
        verifyInput(input);

        //verify that user information is correct
        UserData user;
        user = daos.userDao().getUser(request.username(), request.password());
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
        authorization = daos.authDao().getAuth(request.authToken());
        if(authorization==null){
            //make throw unauthorized exception
            throw new ResponseException("Error: unauthorized",401);
        }
        daos.authDao().deleteAuth(authorization);

        return new LogoutResult();
    }


    private AuthData addAuthData(String username){
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken,username);
        daos.authDao().createAuth(auth);

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
