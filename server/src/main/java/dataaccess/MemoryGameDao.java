package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.ArrayList;

public class MemoryGameDao implements GameDao{
    private static ArrayList<GameData> games = new ArrayList<>();
    private static int nextID = 1;
    //not sure if games should be static yet
    public void createGame(GameData game) throws DataAccessException{
        games.add(game);
    }
    public GameData getGame(int gameID) throws DataAccessException{
        for(GameData game : games){
            if(game.gameID() == gameID){
                return game;
            }
        }
        return null;
    }
    public Collection<GameData> listGames() throws DataAccessException{
        return games;
        //note: GameData toString method is not overriden so this will be ugly
    }
    public void updateGame(int gameID, GameData game) throws DataAccessException{
        for(int i = 0; i < games.size(); i++){
            if(games.get(i).gameID() == gameID){
                games.set(i,game);
                break;
            }
        }
    }
    public void clearGameData(){
        games.clear();
    }
    public int getID(){
        return nextID++;
    }
}
