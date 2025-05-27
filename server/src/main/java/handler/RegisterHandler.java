package handler;

import com.google.gson.Gson;
import resultrequest.RegisterRequest;
import resultrequest.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler {
    public static Object registerUser(Request req, Response res) throws Exception {
        var request = new Gson().fromJson(req.body(), RegisterRequest.class);
        UserService service = new UserService();
        RegisterResult result = service.register(request);
        return JsonHandler.serialize(result);

    }
}
