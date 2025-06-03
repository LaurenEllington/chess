package handler;

import com.google.gson.Gson;
import resultrequest.LoginRequest;
import resultrequest.LoginResult;
import server.DataAccessClasses;
import service.ResponseException;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    DataAccessClasses daos;
    public LoginHandler(DataAccessClasses daos){
        this.daos=daos;
    }
    public Object loginUser(Request req, Response res) throws ResponseException {
        var request = new Gson().fromJson(req.body(), LoginRequest.class);
        UserService service = new UserService(daos);
        LoginResult result = service.login(request);
        return JsonHandler.serialize(result);

    }
}
