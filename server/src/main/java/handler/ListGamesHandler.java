package handler;

import service.GameService;
import resultrequest.ListGamesResult;
import resultrequest.ListGamesRequest;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    public static Object listGames(Request req, Response res) throws Exception {
        ListGamesRequest request = new ListGamesRequest(req.headers("Authorization"));
        GameService service = new GameService();
        ListGamesResult result = service.listGames(request);
        return JsonHandler.serialize(result);
    }
}
