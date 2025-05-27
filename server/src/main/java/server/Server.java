package server;

import chess.*;
import handler.RegisterHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        //initialize all handlers
        //once handlers are created, listen for incoming requests
        //figure out what kind of request is being called then forward the request to handler objects to process them
        //one handler for each function
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        Spark.delete("/db",(req, res) -> {
            res.status(200); //not sure if this is necessary
            return "{}";
        });
        Spark.post("/user",RegisterHandler::registerUser);
        //login
        //create session route
        //create handler
        Spark.awaitInitialization();
        return Spark.port();
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
