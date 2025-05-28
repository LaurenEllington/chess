package handler;

import com.google.gson.Gson;
import resultrequest.LoginRequest;
import resultrequest.LoginResult;
import service.ResponseException;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    public static Object loginUser(Request req, Response res) throws ResponseException {
        var request = new Gson().fromJson(req.body(), LoginRequest.class);
        UserService service = new UserService();
        LoginResult result = service.login(request);
        return JsonHandler.serialize(result);

    }
}
