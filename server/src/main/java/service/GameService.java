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
        ArrayList<GameData> games = new ArrayList<>(gameDao.listGames());
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
    public JoinGameResult joinGame(JoinGameRequest request) throws Exception{
        AuthData authorization = authorize(request.authToken());

        //verify that gameID is valid and has an associated game
        GameData game = gameDao.getGame(request.gameID());
        if(game==null){
            throw new Exception("Invalid gameID.");
        }

        //verify that requested color isn't taken
        if(request.playerColor()==ChessGame.TeamColor.BLACK&&game.blackUsername()!=null){
            throw new AlreadyTakenException("Color already taken.");
        }
        else if(request.playerColor()==ChessGame.TeamColor.WHITE&&game.whiteUsername()!=null){
            throw new AlreadyTakenException("Color already taken.");
        }

        //create gamedata to add requester as specified color
        //debug: add host as other color
        GameData newGame;
        if(request.playerColor()==ChessGame.TeamColor.BLACK) {
            newGame = new GameData(request.gameID(), game.whiteUsername(),authorization.username(),game.gameName(),game.game());
        }
        else{
            newGame = new GameData(request.gameID(),authorization.username(),game.blackUsername(),game.gameName(),game.game());
        }

        //add gamedata to database
        gameDao.updateGame(request.gameID(),newGame);
        return new JoinGameResult();
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
