package dataaccess;

import chess.ChessGame;
import handler.JsonHandler;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import passoff.model.TestUser;
import server.Server;
import service.ResponseException;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUnitTests {
    private static final UserData testUser =
            new UserData("testUserName","testPassword","test@gmail.com");
    GameData testGame =
            new GameData(0,null,null,"testGame",new ChessGame());

    private static final MySqlAuthDao authDao = new MySqlAuthDao();
    private static final MySqlGameDao gameDao = new MySqlGameDao();
    private static final MySqlUserDao userDao = new MySqlUserDao();
    public static final String insertError = "Error: unable to insert into database: %s";
    public static final String getError = "Error: could not get %s from database";

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
    public void testInvalidUser() throws DataAccessException{
        addValidUser();
        boolean throwed = false;
        try {
            userDao.createUser(testUser);
        } catch (ResponseException ex) {
            throwed = true;
        }
        Assertions.assertTrue(throwed,
                "Error: adding a duplicate user to the database did not throw an exception");
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
    public void testCreateGame() {
        Assertions.assertDoesNotThrow( () -> gameDao.createGame(testGame));

        var statement = "SELECT username, password, email FROM user WHERE username=?";
    }
    private void addValidUser() throws DataAccessException{
        String hash = BCrypt.hashpw(testUser.password(),BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,testUser.username());
                ps.setString(2,hash);
                ps.setString(3,testUser.email());
                ps.executeUpdate();
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error: could not access database " + ex.getMessage());
        }
    }
    private void compareUser(String dbUsername, String dbPassword, String dbEmail){
        String errorMessage = "Error: %s added into database is different than username provided";
        Assertions.assertEquals(dbUsername,testUser.username(), String.format(errorMessage,"username"));
        Assertions.assertNotEquals(dbPassword,testUser.password(), "Error: password not encrypted");
        Assertions.assertTrue(() -> BCrypt.checkpw(testUser.password(),dbPassword),
                String.format(errorMessage,"password"));
        Assertions.assertEquals(dbEmail,testUser.email(),String.format(errorMessage,"email"));
    }
}
