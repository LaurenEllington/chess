package handler;

import com.google.gson.Gson;
import resultrequest.CreateGameResult;
import resultrequest.CreateGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    public static Object createGame(Request req, Response res) throws Exception {
        CreateGameRequest request = new Gson().fromJson(req.body(), CreateGameRequest.class);
        request = new CreateGameRequest(request.gameName(),req.headers("Authorization"));
        GameService service = new GameService();
        CreateGameResult result = service.createGame(request);
        return JsonHandler.serialize(result);
    }
}
