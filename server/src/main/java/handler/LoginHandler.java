package handler;

import com.google.gson.Gson;
import resultrequest.LoginRequest;
import resultrequest.LoginResult;
import service.UserService;

public class LoginHandler {
    public String handleLogin(String input) throws Exception{
        Gson gson = new Gson();
        UserService service = new UserService();
        LoginRequest request = gson.fromJson(input, LoginRequest.class);
        LoginResult result = service.login(request);
        return JsonHandler.serialize(result);
    }
}
