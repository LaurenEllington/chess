package service;

import dataaccess.MemoryAuthDao;
import dataaccess.MemoryUserDao;
import model.UserData;
import model.AuthData;

import java.util.ArrayList;

record RegisterResult(String username, String authToken) {}
record RegisterRequest(String username, String password, String email) {}

public class UserService {
    RegisterResult register(RegisterRequest request) throws Exception{
        //verify that requested username, password, or email is not null or empty
        ArrayList<String> input = new ArrayList<String>();
        input.add(request.username());
        input.add(request.password());
        input.add(request.email());
        verifyInput(input,"username, password, or email");

        //verify that username isn't already taken
        MemoryUserDao userDao = new MemoryUserDao();
        if(userDao.getUser(request.username())!=null){
            throw new AlreadyTakenException("Username " + request.username() + " is already taken.");
        }

        //create userData object and add to database
        UserData user = new UserData(request.username(),request.password(),request.email());
        userDao.createUser(user);

        //log in the user
        String authToken = "";
        //figure out how to create a unique authtoken?????
        MemoryAuthDao authDao = new MemoryAuthDao();
        AuthData auth = new AuthData(authToken,user.username());
        authDao.createAuth(auth);


        //create result
        return new RegisterResult(user.username(),authToken);
    }

    //make throw bad request exception
    void verifyInput(ArrayList<String> input, String inputType) throws Exception{
        for(String str : input){
            if(str==null||str.isEmpty()){
                throw new Exception("Invalid "+inputType+".");
            }
        }
    }
}
