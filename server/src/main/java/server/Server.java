package server;

import chess.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        Spark.delete("/db",(req, res) -> {
            res.status(200); //not sure if this is necessary
            return "{}";
        });
        //Spark.post("/user", (req, res) -> handleRegistration(req, res));
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
