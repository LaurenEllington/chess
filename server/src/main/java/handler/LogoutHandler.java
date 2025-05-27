package handler;

import com.google.gson.Gson;
import resultrequest.LogoutRequest;
import resultrequest.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Map;

public class LogoutHandler {
    //get authorization header -> authtoken
    //take info out of json and authtoken and turn into logoutrequest object
    //pass data to service
    //call userservice logout method
    //turn result object into json string
    public static Object logoutUser(Request req, Response res) throws Exception {
        LogoutRequest request = new LogoutRequest(req.headers("Authorization"));
        UserService service = new UserService();
        LogoutResult result = service.logout(request);
        return JsonHandler.serialize(result);
    }
    //appropriate status code
}
