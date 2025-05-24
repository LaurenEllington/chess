package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import model.AuthData;
import model.GameData;
import resultrequest.*;
import java.util.ArrayList;

public class GameService {
    private MemoryAuthDao authDao = new MemoryAuthDao();
    private MemoryGameDao gameDao = new MemoryGameDao();

    public ListGamesResult listGames(ListGamesRequest request) throws Exception{
        //verify user identity
        authorize(request.authToken());

        return new ListGamesResult(gameDao.listGames());
    }
    public CreateGameResult createGame(CreateGameRequest request) throws Exception{
        authorize(request.authToken());

        //verify that gamename is valid
        if(request.gameName()==null||request.gameName().isEmpty()){
            throw new Exception("Invalid gamename.");
        }

        //verify that gamename isn't already taken
        ArrayList<GameData> games = new ArrayList<GameData>(gameDao.listGames());
        //debug note: make sure the collection cast works
        for(GameData game : games){
            if(game.gameName().equals(request.gameName())){
                //alreadytaken exception
                throw new Exception("Gamename already taken.");
            }
        }

        //create gamedata object and add to database
        GameData game = new GameData(generateID(),null,null,request.gameName(),new ChessGame());
        gameDao.createGame(game);

        //create result
        return new CreateGameResult(game.gameID());
    }
    private int generateID(){
        return 1234;
    }
    private AuthData authorize(String authToken) throws Exception{
        AuthData authorization = authDao.getAuth(authToken);
        if(authorization==null){
            //make throw unauthorized exception
            throw new Exception("Unauthorized.");
        }
        return authorization;
    }
}
