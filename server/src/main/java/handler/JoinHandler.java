package handler;

import com.google.gson.Gson;
import resultrequest.JoinGameRequest;
import resultrequest.JoinGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import service.ResponseException;

public class JoinHandler {
    public static Object joinGame(Request req, Response res) throws ResponseException {
        JoinGameRequest request = new Gson().fromJson(req.body(), JoinGameRequest.class);
        System.out.println(request);
        request = new JoinGameRequest(request.playerColor(),request.gameID(),req.headers("Authorization"));
        GameService service = new GameService();
        JoinGameResult result = service.joinGame(request);
        return JsonHandler.serialize(result);
        //this lists way too much due to tostring of chessboard
    }
}
