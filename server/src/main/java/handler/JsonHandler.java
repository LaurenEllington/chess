package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import resultrequest.LogoutRequest;
import resultrequest.LogoutResult;
import service.UserService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class JsonHandler {
    //deal with all http communication details
    //when request comes in (in json format) with authtoken
    //parse into request
    //send to service
    //parse result into json and send back
    public static String serialize(Object result){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(result);
    }
}
