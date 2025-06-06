package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import handler.JsonHandler;
import model.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class DatabaseUnitTests {
    private static final UserData TEST_USER =
            new UserData("testUserName","testPassword","test@gmail.com");
    private static final AuthData TEST_AUTH =
            new AuthData(UUID.randomUUID().toString(),"testUserName");
    private static final AuthData TEST_AUTH2 =
            new AuthData(UUID.randomUUID().toString(),"testUserName2");
    private static final GameData TEST_GAME =
            new GameData(1,null,"smab","testGame",new ChessGame());
    private static final GameData TEST_GAME2 =
            new GameData(2,"soHungry",null,"testGame2",new ChessGame());

    private static final MySqlAuthDao AUTH_DAO = new MySqlAuthDao();
    private static final MySqlGameDao GAME_DAO = new MySqlGameDao();
    private static final MySqlUserDao USER_DAO = new MySqlUserDao();
    private static final String INSERT_ERROR = "Error: unable to insert into database: %s";
    private static final String GET_ERROR = "Error: could not get %s from database";
    private static final String INCONSISTENT_ERROR = "Error: %s added into database is not the same as original %s";
    private static final String DUPLICATE_ERROR = "Error: adding a duplicate %s to the database did not throw an exception";

    //should either recreate tables or end transaction
    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }
    @AfterEach
    public void takeDown() throws DataAccessException {
        DatabaseManager.dropDatabase();
    }
    @Test
    public void testAddUser() throws DataAccessException{
        Assertions.assertDoesNotThrow( () -> USER_DAO.createUser(TEST_USER),
                String.format(INSERT_ERROR, TEST_USER));
        var sql = "SELECT username, password, email FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1,TEST_USER.username());
                ResultSet rs = ps.executeQuery();
                Assertions.assertTrue(rs.next(),"Error: No data added");
                String dbUsername = rs.getString(1);
                String dbPassword = rs.getString(2);
                String dbEmail = rs.getString(3);
                compareUser(dbUsername,dbPassword,dbEmail);
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }

    }
    @Test
    public void testDuplicateUser() throws DataAccessException{
        addValidUser();
        try {
            USER_DAO.createUser(TEST_USER);
            Assertions.fail(String.format(DUPLICATE_ERROR,"user"));
        } catch (ResponseException ignored) {}
    }
    @Test
    public void testGetUser() throws DataAccessException{
        addValidUser();
        UserData dbUser = Assertions.assertDoesNotThrow(() ->
                USER_DAO.getUser(TEST_USER.username()),String.format(GET_ERROR,TEST_USER));
        compareUser(dbUser.username(),dbUser.password(),dbUser.email());
    }
    @Test
    public void testGetNonexistentUser(){
        Assertions.assertNull(USER_DAO.getUser(TEST_USER.username()),
                "Error: calling getUser on a nonexistent user did not return null");
    }
    @Test
    public void testRightPassword() throws DataAccessException{
        addValidUser();
        UserData dbUser = Assertions.assertDoesNotThrow(() ->
                USER_DAO.getUser(TEST_USER.username(),TEST_USER.password()),
                String.format(GET_ERROR,TEST_USER));
        compareUser(dbUser.username(),dbUser.password(),dbUser.email());
    }
    @Test
    public void testWrongPassword() throws DataAccessException{
        addValidUser();
        Assertions.assertNull(USER_DAO.getUser(TEST_USER.username(),"invalid"),
                "Error: retrieved user with a password that was incorrect");
    }
    @Test
    public void clearUsers() throws DataAccessException {
        addValidUser();
        try {
            USER_DAO.clearUserData();
        } catch (ResponseException e){
            Assertions.fail("Error: "+e.getMessage());
        }
        var sql = "SELECT username, password, email FROM user";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                Assertions.assertFalse(rs.next(),"Error: data still in table after clearUserData");
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
    @Test
    public void testAddAuth() throws DataAccessException{
        Assertions.assertDoesNotThrow( () -> AUTH_DAO.createAuth(TEST_AUTH),
                String.format(INSERT_ERROR, TEST_AUTH));
        var sql = "SELECT authToken, username FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, TEST_AUTH.authToken());
                ResultSet rs = ps.executeQuery();
                Assertions.assertTrue(rs.next(),"Error: No data added");
                compareAuth(rs.getString(1),rs.getString(2));
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
    @Test
    public void testAddDuplicateAuth() throws DataAccessException{
        addValidAuth(TEST_AUTH);
        try {
            AUTH_DAO.createAuth(TEST_AUTH);
            Assertions.fail(String.format(DUPLICATE_ERROR,"auth"));
        } catch (ResponseException ignored) {}
    }
    @Test
    public void testGetAuth() throws DataAccessException{
        addValidAuth(TEST_AUTH);
        AuthData dbAuth = Assertions.assertDoesNotThrow(() ->
                AUTH_DAO.getAuth(TEST_AUTH.authToken()),String.format(GET_ERROR, TEST_AUTH));
        compareAuth(dbAuth.authToken(), dbAuth.username());
    }
    @Test
    public void testGetNonexistentAuth() throws DataAccessException{
        Assertions.assertNull(AUTH_DAO.getAuth(TEST_AUTH.authToken()),
                "Error: calling getAuth on a nonexistent auth did not return null");
        addValidAuth(TEST_AUTH);
        Assertions.assertNull(AUTH_DAO.getAuth(UUID.randomUUID().toString()),
                "Error: calling getAuth on a nonexistent auth did not return null");
    }
    @Test
    public void deleteAuth() throws DataAccessException {
        addValidAuth(TEST_AUTH);
        addValidAuth(TEST_AUTH2);
        try {
            AUTH_DAO.deleteAuth(TEST_AUTH);
        } catch (ResponseException e){
            Assertions.fail("Error: "+e.getMessage());
        }
        var sql = "SELECT authToken, username FROM auth";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                Assertions.assertTrue(rs.next(),"Error: no data at all in auth table after deleteAuth");
                Assertions.assertFalse(rs.next(),"Error: data still in table after deleteAuth");
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
    @Test
    public void clearAuths() throws DataAccessException {
        addValidAuth(TEST_AUTH);
        AUTH_DAO.clearAuthData();
        var sql = "SELECT authToken, username FROM auth";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                Assertions.assertFalse(rs.next(),"Error: data still in table after clearAuthData");
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
    @Test
    public void testCreateGame() throws DataAccessException{
        Assertions.assertDoesNotThrow(() -> GAME_DAO.createGame(TEST_GAME),
                String.format(INSERT_ERROR, TEST_GAME));
        var game = readGame();
        compareGame(game.gameID(),game.whiteUsername(),game.blackUsername(),game.gameName(),
                JsonHandler.serialize(game.game()));
    }
    @Test
    public void testDuplicateGame(){
        try{
            addValidGame(TEST_GAME);
            GAME_DAO.createGame(TEST_GAME);
            Assertions.fail(String.format(DUPLICATE_ERROR,"game"));
        } catch (DataAccessException ignored) {}
    }
    @Test
    public void testGetGame() throws DataAccessException{
        addValidGame(TEST_GAME);
        GameData dbGame = Assertions.assertDoesNotThrow(() ->
                GAME_DAO.getGame(1),String.format(GET_ERROR,TEST_GAME));
        compareGame(dbGame.gameID(),dbGame.whiteUsername(),dbGame.blackUsername(),dbGame.gameName(),
                JsonHandler.serialize(dbGame.game()));
    }
    @Test
    public void testGetInvalidGame() throws DataAccessException{
        addValidGame(TEST_GAME);
        Assertions.assertNull(GAME_DAO.getGame(0),
                "Error: using getGame with an invalid gameID does not return null");
    }
    @Test
    public void testListGames() throws DataAccessException{
        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(TEST_GAME);
        expected.add(TEST_GAME2);
        addValidGame(TEST_GAME);
        addValidGame(TEST_GAME2);
        Assertions.assertEquals(expected,GAME_DAO.listGames(),
                "Error: listGames does not return expected output");
    }
    //dunno what to do for a negative listGames test
    @Test
    public void testUpdateGame() throws DataAccessException{
        addValidGame(TEST_GAME);
        GameData expected = new GameData(
                1,"wob",TEST_GAME.blackUsername(),TEST_GAME.gameName(),TEST_GAME.game());
        try{
            GAME_DAO.updateGame(1,expected);
        } catch (ResponseException ex) {
            Assertions.fail("Error: couldn't update game");
        }
        var game = readGame();
        Assertions.assertEquals(expected.gameID(),game.gameID());
        Assertions.assertEquals(expected.whiteUsername(),game.whiteUsername());
        Assertions.assertEquals(expected.blackUsername(),game.blackUsername());
        Assertions.assertEquals(expected.gameName(),game.gameName());
        Assertions.assertEquals(JsonHandler.serialize(expected.game()),JsonHandler.serialize(game.game()));
    }
    @Test
    public void testInvalidUpdate() throws DataAccessException{
        addValidGame(TEST_GAME);
        GameData expected = new GameData(
                0,"wob",TEST_GAME.blackUsername(),TEST_GAME.gameName(),TEST_GAME.game());
        try{
            GAME_DAO.updateGame(expected.gameID(),expected);
        } catch (ResponseException ex) {
            Assertions.fail("Error: couldn't update game, invalid id");
        }
    }
    @Test
    public void clearGames() throws DataAccessException {
        addValidGame(TEST_GAME);
        addValidGame(TEST_GAME2);
        GAME_DAO.clearGameData();
        var sql = "SELECT gameId FROM game";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                Assertions.assertFalse(rs.next(),"Error: data still in table after clearGameData");
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
    private void addValidUser() throws DataAccessException{
        String hash = BCrypt.hashpw(TEST_USER.password(),BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        addValid(statement,TEST_USER.username(),hash,TEST_USER.email());
    }
    private void addValidAuth(AuthData a) throws DataAccessException{
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        addValid(statement,a.authToken(),a.username());
    }
    private void addValidGame(GameData game) throws DataAccessException{
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
        addValid(statement, game.whiteUsername(),game.blackUsername(),game.gameName(),
                JsonHandler.serialize(game.game()));
    }
    private void addValid(String statement, String... params) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for(int i = 0; i < params.length; i++){
                    ps.setString(i+1,params[i]);
                }
                ps.executeUpdate();
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: could not access database " + ex.getMessage());
        }
    }
    private void compareUser(String dbUsername, String dbPassword, String dbEmail){
        Assertions.assertEquals(dbUsername,TEST_USER.username(), String.format(INCONSISTENT_ERROR,"username","username"));
        Assertions.assertNotEquals(dbPassword,TEST_USER.password(), "Error: password not encrypted");
        Assertions.assertTrue(() -> BCrypt.checkpw(TEST_USER.password(),dbPassword),
                String.format(INCONSISTENT_ERROR,"password","password"));
        Assertions.assertEquals(dbEmail,TEST_USER.email(),String.format(INCONSISTENT_ERROR,"email","email"));
    }
    private void compareAuth(String dbAuthToken, String dbUsername){
        Assertions.assertEquals(dbAuthToken, TEST_AUTH.authToken(),
                String.format(INCONSISTENT_ERROR,"authToken","authToken"));
        Assertions.assertEquals(dbUsername, TEST_AUTH.username(),
                String.format(INCONSISTENT_ERROR,"username","username"));
    }
    private void compareGame(int dbGameId, String dbWhiteUser, String dbBlackUser, String dbGameName, String dbGame){
        Assertions.assertEquals(1, dbGameId,String.format(INCONSISTENT_ERROR,"gameID","gameID"));
        Assertions.assertEquals(dbWhiteUser, TEST_GAME.whiteUsername(),
                String.format(INCONSISTENT_ERROR,"whiteUsername","whiteUsername"));
        Assertions.assertEquals(dbBlackUser, TEST_GAME.blackUsername(),
                String.format(INCONSISTENT_ERROR,"blackUsername","blackUsername"));
        Assertions.assertEquals(dbGameName, TEST_GAME.gameName(),
                String.format(INCONSISTENT_ERROR,"gameName","gameName"));
        Assertions.assertEquals(JsonHandler.serialize(TEST_GAME.game()),dbGame,
                String.format(INCONSISTENT_ERROR,"chessGame","chessGame"));
    }
    private GameData readGame() throws DataAccessException{
        var sql = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1,1);
                ResultSet rs = ps.executeQuery();
                Assertions.assertTrue(rs.next(),"Error: No game with that gameID");
                var game = new Gson().fromJson(rs.getString(5), ChessGame.class);
                return new GameData(rs.getInt(1),
                        rs.getString(2),rs.getString(3),rs.getString(4),game);
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
}
