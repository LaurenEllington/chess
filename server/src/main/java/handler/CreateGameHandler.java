package handler;

import com.google.gson.Gson;
import resultrequest.CreateGameResult;
import resultrequest.CreateGameRequest;
import server.DataAccessClasses;
import service.GameService;
import spark.Request;
import spark.Response;
import service.ResponseException;

public class CreateGameHandler {
    private DataAccessClasses daos;
    public CreateGameHandler(DataAccessClasses daos){
        this.daos=daos;
    }
    public Object createGame(Request req, Response res) throws ResponseException {
        CreateGameRequest request = new Gson().fromJson(req.body(), CreateGameRequest.class);
        request = new CreateGameRequest(request.gameName(),req.headers("Authorization"));
        GameService service = new GameService(daos);
        CreateGameResult result = service.createGame(request);
        return JsonHandler.serialize(result);
    }
}
