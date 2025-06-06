package server;

import dataaccess.*;
import handler.*;
import spark.*;
import service.ResponseException;

public class Server {
    private final String dataAccessType;
    public Server(String dataAccessType){
        this.dataAccessType=dataAccessType;

    }
    public Server(){
        this("sql"); //default dataaccesstype
    }
    public int run(int desiredPort) {
        if(desiredPort!=0){
            Spark.port(desiredPort);
        }
        else{
            Spark.port(8080);
        }
        //initialize all handlers
        //once handlers are created, listen for incoming requests
        //figure out what kind of request is being called then forward the request to handler objects to process them
        //one handler for each function
        DataAccessClasses daos;
        try {
            daos = getDaos();
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage(),500);
        }
            Spark.staticFiles.location("web");
            // Register your endpoints and handle exceptions here.
            ClearHandler clearHandler = new ClearHandler(daos);
            Spark.delete("/db", clearHandler::clearData);
            LogoutHandler logoutHandler = new LogoutHandler(daos);
            Spark.delete("/session", logoutHandler::logoutUser);
            RegisterHandler registerHandler = new RegisterHandler(daos);
            Spark.post("/user", registerHandler::registerUser);
            LoginHandler loginHandler = new LoginHandler(daos);
            Spark.post("/session", loginHandler::loginUser);
            CreateGameHandler createGameHandler = new CreateGameHandler(daos);
            Spark.post("/game", createGameHandler::createGame);
            ListGamesHandler listGamesHandler = new ListGamesHandler(daos);
            Spark.get("/game", listGamesHandler::listGames);
            JoinHandler joinHandler = new JoinHandler(daos);
            Spark.put("/game", joinHandler::joinGame);
            Spark.exception(ResponseException.class, this::exceptionHandler);
            Spark.awaitInitialization();
            return Spark.port();
        }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    private DataAccessClasses getDaos() throws DataAccessException{
        DataAccessClasses daos;
        if (dataAccessType.equals("sql")) {
            daos = new DataAccessClasses(new MySqlUserDao(),new MySqlGameDao(),new MySqlAuthDao());
        } else {
            daos = new DataAccessClasses(new MemoryUserDao(),new MemoryGameDao(),new MemoryAuthDao());
        }
        return daos;
    }
    private void exceptionHandler(ResponseException e, Request req, Response res){
        res.status(e.getStatus());
        res.body(e.toJson());
    }
}
