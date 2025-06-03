package handler;

import com.google.gson.JsonObject;
import resultrequest.ClearResult;
import server.DataAccessClasses;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    private DataAccessClasses daos;
    public ClearHandler(DataAccessClasses daos){
        this.daos = daos;
    }
    public Object clearData(Request req, Response res) throws Exception {
        ClearResult result = new ClearService(daos).clear();
        res.status(200);
        return JsonHandler.serialize(result);
    }

}
