package handler;

import server.DataAccessClasses;
import service.GameService;
import resultrequest.ListGamesResult;
import resultrequest.ListGamesRequest;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    private DataAccessClasses daos;
    public ListGamesHandler(DataAccessClasses daos){
        this.daos=daos;
    }
    public Object listGames(Request req, Response res) throws Exception {
        ListGamesRequest request = new ListGamesRequest(req.headers("Authorization"));
        GameService service = new GameService(daos);
        ListGamesResult result = service.listGames(request);
        return JsonHandler.serialize(result);
        //this lists way too much due to tostring of chessboard
    }
}
