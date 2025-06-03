package handler;

import com.google.gson.Gson;
import resultrequest.RegisterRequest;
import resultrequest.RegisterResult;
import server.DataAccessClasses;
import service.UserService;
import spark.Request;
import spark.Response;
import service.ResponseException;

public class RegisterHandler {
    private DataAccessClasses daos;
    public RegisterHandler(DataAccessClasses daos){
        this.daos=daos;
    }
    public Object registerUser(Request req, Response res) throws ResponseException{
        var request = new Gson().fromJson(req.body(), RegisterRequest.class);
        UserService service = new UserService(daos);
        RegisterResult result = service.register(request);
        return JsonHandler.serialize(result);
    }
}
