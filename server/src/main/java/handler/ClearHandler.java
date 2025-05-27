package handler;

import com.google.gson.JsonObject;
import resultrequest.ClearResult;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    public static Object clearData(Request req, Response res) throws Exception {
        ClearResult result = new ClearService().clear();
        res.status(200);
        return JsonHandler.serialize(result);
    }

}
