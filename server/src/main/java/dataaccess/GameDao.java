package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDao {
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, GameData game) throws DataAccessException;
    void clearGameData();
}
