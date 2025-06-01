package dataaccess;

import model.GameData;

import java.util.Collection;

public class MySqlGameDao implements GameDao{
    public void createGame(GameData game) throws DataAccessException{}
    public GameData getGame(int gameID) throws DataAccessException{
        return null;
    }
    public Collection<GameData> listGames() throws DataAccessException{
        return null;
    }
    public void updateGame(int gameID, GameData game) throws DataAccessException{}
    public void clearGameData(){}
}
