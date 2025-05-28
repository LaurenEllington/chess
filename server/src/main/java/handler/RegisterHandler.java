package handler;

import com.google.gson.Gson;
import resultrequest.RegisterRequest;
import resultrequest.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import service.ResponseException;

public class RegisterHandler {
    public static Object registerUser(Request req, Response res) throws ResponseException{
        var request = new Gson().fromJson(req.body(), RegisterRequest.class);
        UserService service = new UserService();
        RegisterResult result = service.register(request);
        return JsonHandler.serialize(result);
    }
}
