package handler;

import resultrequest.LogoutRequest;
import resultrequest.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;
import service.ResponseException;


public class LogoutHandler {
    public static Object logoutUser(Request req, Response res) throws ResponseException {
        LogoutRequest request = new LogoutRequest(req.headers("Authorization"));
        UserService service = new UserService();
        LogoutResult result = service.logout(request);
        return JsonHandler.serialize(result);
    }
}
