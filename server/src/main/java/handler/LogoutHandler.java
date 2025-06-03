package handler;

import resultrequest.LogoutRequest;
import resultrequest.LogoutResult;
import server.DataAccessClasses;
import service.UserService;
import spark.Request;
import spark.Response;
import service.ResponseException;


public class LogoutHandler {
    DataAccessClasses daos;
    public LogoutHandler(DataAccessClasses daos){
        this.daos=daos;
    }
    public Object logoutUser(Request req, Response res) throws ResponseException {
        LogoutRequest request = new LogoutRequest(req.headers("Authorization"));
        UserService service = new UserService(daos);
        LogoutResult result = service.logout(request);
        return JsonHandler.serialize(result);
    }
}
