package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.ResponseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseUnitTests {
    private static final UserData testUser =
            new UserData("testUserName","testPassword","test@gmail.com");
    private static final AuthData testAuth =
            new AuthData(UUID.randomUUID().toString(),"testUserName");
    private GameData testGame =
            new GameData(0,null,null,"testGame",new ChessGame());

    private static final MySqlAuthDao authDao = new MySqlAuthDao();
    private static final MySqlGameDao gameDao = new MySqlGameDao();
    private static final MySqlUserDao userDao = new MySqlUserDao();
    private static final String insertError = "Error: unable to insert into database: %s";
    private static final String getError = "Error: could not get %s from database";
    private static final String inconsistentError = "Error: %s added into database is not the same as original %s";
    private static final String duplicateError = "Error: adding a duplicate %s to the database did not throw an exception";

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
        Assertions.assertDoesNotThrow( () -> userDao.createUser(testUser),
                String.format(insertError, testUser));
        var sql = "SELECT username, password, email FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1,testUser.username());
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
            userDao.createUser(testUser);
            Assertions.fail(String.format(duplicateError,"user"));
        } catch (ResponseException ignored) {}
    }
    @Test
    public void testGetUser() throws DataAccessException{
        addValidUser();
        UserData dbUser = Assertions.assertDoesNotThrow(() ->
                userDao.getUser(testUser.username()),String.format(getError,testUser));
        compareUser(dbUser.username(),dbUser.password(),dbUser.email());
    }
    @Test
    public void testGetNonexistentUser(){
        Assertions.assertNull(userDao.getUser(testUser.username()),
                "Error: calling getUser on a nonexistent user did not return null");
    }
    @Test
    public void testRightPassword() throws DataAccessException{
        addValidUser();
        UserData dbUser = Assertions.assertDoesNotThrow(() ->
                userDao.getUser(testUser.username(),testUser.password()),
                String.format(getError,testUser));
        compareUser(dbUser.username(),dbUser.password(),dbUser.email());
    }
    @Test
    public void testWrongPassword() throws DataAccessException{
        addValidUser();
        Assertions.assertNull(userDao.getUser(testUser.username(),"invalid"),
                "Error: retrieved user with a password that was incorrect");
    }
    @Test
    public void clearUsers() throws DataAccessException {
        addValidUser();
        try {
            userDao.clearUserData();
        } catch (ResponseException e){
            Assertions.fail("Error: "+e.getMessage());
        }
        var sql = "SELECT username, password, email FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1,testUser.username());
                ResultSet rs = ps.executeQuery();
                Assertions.assertFalse(rs.next(),"Error: data still in table after clearUserData");
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
    @Test
    public void testAddAuth() throws DataAccessException{
        Assertions.assertDoesNotThrow( () -> authDao.createAuth(testAuth),
                String.format(insertError, testAuth));
        var sql = "SELECT authToken, username FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1,testAuth.authToken());
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
        addValidAuth();
        try {
            authDao.createAuth(testAuth);
            Assertions.fail(String.format(duplicateError,"auth"));
        } catch (ResponseException ignored) {}
    }
    @Test
    public void testGetAuth() throws DataAccessException{
        addValidAuth();
        AuthData dbAuth = Assertions.assertDoesNotThrow(() ->
                authDao.getAuth(testAuth.authToken()),String.format(getError,testAuth));
        compareAuth(dbAuth.authToken(), dbAuth.username());
    }
    @Test
    public void testGetNonexistentAuth() throws DataAccessException{
        Assertions.assertNull(authDao.getAuth(testAuth.authToken()),
                "Error: calling getAuth on a nonexistent auth did not return null");
        addValidAuth();
        Assertions.assertNull(authDao.getAuth(UUID.randomUUID().toString()),
                "Error: calling getAuth on a nonexistent auth did not return null");
    }
    @Test
    public void deleteAuth() throws DataAccessException {
        addValidAuth();
        try {
            authDao.deleteAuth(testAuth);
        } catch (ResponseException e){
            Assertions.fail("Error: "+e.getMessage());
        }
        var sql = "SELECT authToken, username FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1,testAuth.authToken());
                ResultSet rs = ps.executeQuery();
                Assertions.assertFalse(rs.next(),"Error: data still in table after clearUserData");
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
    @Test
    public void testCreateGame() {
        Assertions.assertDoesNotThrow( () -> gameDao.createGame(testGame));

        var statement = "SELECT username, password, email FROM user WHERE username=?";
    }
    private void addValidUser() throws DataAccessException{
        String hash = BCrypt.hashpw(testUser.password(),BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        addValid(statement,testUser.username(),hash,testUser.email());
    }
    private void addValidAuth() throws DataAccessException{
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        addValid(statement,testAuth.authToken(),testAuth.username());
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
        Assertions.assertEquals(dbUsername,testUser.username(), String.format(inconsistentError,"username","username"));
        Assertions.assertNotEquals(dbPassword,testUser.password(), "Error: password not encrypted");
        Assertions.assertTrue(() -> BCrypt.checkpw(testUser.password(),dbPassword),
                String.format(inconsistentError,"password","password"));
        Assertions.assertEquals(dbEmail,testUser.email(),String.format(inconsistentError,"email","email"));
    }
    private void compareAuth(String dbAuthToken, String dbUsername){
        Assertions.assertEquals(dbAuthToken,testAuth.authToken(),
                String.format(inconsistentError,"authToken","authToken"));
        Assertions.assertEquals(dbUsername,testAuth.username(),
                String.format(inconsistentError,"username","username"));
    }
}
