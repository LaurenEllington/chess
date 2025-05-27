package server;

import chess.*;
import handler.*;
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
        Spark.delete("/db", ClearHandler::clearData);
        Spark.delete("/session", LogoutHandler::logoutUser);
        Spark.post("/user",RegisterHandler::registerUser);
        Spark.post("/session", LoginHandler::loginUser);
        Spark.get("/game",ListGamesHandler::listGames);
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
