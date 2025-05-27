package handler;

import com.google.gson.Gson;
import resultrequest.LogoutRequest;
import resultrequest.LogoutResult;
import service.UserService;

public class LogoutHandler {
    //get authorization header -> authtoken
    //take info out of json and authtoken and turn into logoutrequest object
    //pass data to service
    //call userservice logout method
    //turn result object into json string
    public String handleLogout(String input) throws Exception{
        Gson gson = new Gson();
        UserService service = new UserService();
        LogoutRequest request = gson.fromJson(input, LogoutRequest.class);
        LogoutResult result = service.logout(request);
        return JsonHandler.serialize(result);
    }
    //appropriate status code
}
