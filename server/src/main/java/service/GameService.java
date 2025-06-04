package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import model.AuthData;
import model.GameData;
import resultrequest.*;
import server.DataAccessClasses;

import java.util.ArrayList;

public class GameService {
    private DataAccessClasses daos;
    public GameService(DataAccessClasses daos){
        this.daos=daos;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws Exception{
        //verify user identity
        authorize(request.authToken());

        return new ListGamesResult(daos.gameDao().listGames());
    }
    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException{
        authorize(request.authToken());

        //verify that gamename is valid
        if(request.gameName()==null||request.gameName().isEmpty()){
            throw new ResponseException("Error: bad request",400);
        }

        //verify that gamename isn't already taken
        ArrayList<GameData> games;
        try{
            games = new ArrayList<>(daos.gameDao().listGames());
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        for(GameData game : games){
            if(game.gameName().equals(request.gameName())){
                throw new ResponseException("Error: bad request",400);
            }
        }
        //it hates my id generation
        //create gamedata object and add to database
        GameData game = new GameData(generateID(),null,null,request.gameName(),new ChessGame());
        try{
            daos.gameDao().createGame(game);
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        //create result
        return new CreateGameResult(game.gameID());
    }
    public JoinGameResult joinGame(JoinGameRequest request) throws ResponseException{
        AuthData authorization = authorize(request.authToken());

        //verify that gameID is valid and has an associated game
        GameData game;
        try{
            game = daos.gameDao().getGame(request.gameID());
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        if(game==null||request.playerColor()==null){
            throw new ResponseException("Error: bad request",400);
        }
        //verify that requested color isn't taken
        if(request.playerColor()==ChessGame.TeamColor.BLACK&&game.blackUsername()!=null){
            throw new ResponseException("Error: forbidden",403);
        }
        else if(request.playerColor()==ChessGame.TeamColor.WHITE&&game.whiteUsername()!=null){
            throw new ResponseException("Error: forbidden",403);
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
        try{
            daos.gameDao().updateGame(request.gameID(),newGame);
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        return new JoinGameResult();
    }
    private int generateID(){
        return daos.gameDao().getID();
    }
    private AuthData authorize(String authToken) throws ResponseException{
        AuthData authorization;
        try{
            authorization = daos.authDao().getAuth(authToken);
        } catch (DataAccessException e){
            throw new ResponseException(e.getMessage(),500);
        }
        if(authorization==null){
            //make throw unauthorized exception
            throw new ResponseException("Error: unauthorized",401);
        }
        return authorization;
    }
}
