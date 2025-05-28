package service;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import resultrequest.*;

import java.util.ArrayList;

public class ServiceTests {
    ClearService clearService = new ClearService();
    UserService userService = new UserService();
    GameService gameService = new GameService();
    @Test
    @Order(1)
    @DisplayName("Valid User Registration")
    public void registerSuccess(){
        RegisterResult result = registerUser();
        Assertions.assertNotNull(result.username(),"No username was returned in result");
        Assertions.assertEquals("testusername",result.username(),
                "Requested username is not the same as username returned");
        Assertions.assertNotNull(result.authToken(),"No authToken was returned");
        clearService.clear();
    }
    @Test
    @Order(2)
    @DisplayName("Invalid User Registration")
    public void registerFailure() {
        //username is already taken
        RegisterResult result = registerUser();
        Assertions.assertThrows(ResponseException.class,() ->
                registerUser());
        clearService.clear();
    }
    @Test
    @Order(3)
    @DisplayName("Valid User Logout")
    public void logoutSuccess() {
        RegisterResult result = registerUser();
        LogoutRequest request2 = new LogoutRequest(result.authToken());
        Assertions.assertDoesNotThrow( () -> userService.logout(request2));
        clearService.clear();
    }
    @Test
    @Order(4)
    @DisplayName("Invalid User Logout")
    public void logoutFailure() {
        //no authToken
        RegisterResult result = registerUser();
        LogoutRequest request2 = new LogoutRequest("");
        Assertions.assertThrows(ResponseException.class,() ->
                userService.logout(request2));
        clearService.clear();
    }
    @Test
    @Order(5)
    @DisplayName("Valid User Login")
    public void loginSuccess() {
        RegisterResult result = registerUser();
        userService.logout(new LogoutRequest(result.authToken()));
        LoginResult loginResult = userService.login(new LoginRequest(result.username(),"testpassword"));
        Assertions.assertEquals(loginResult.username(),result.username(),
                "Username to log into is not the same as username returned");
        Assertions.assertNotNull(result.authToken(),"No authToken was returned");
        clearService.clear();
    }
    @Test
    @Order(6)
    @DisplayName("Invalid User Login")
    public void loginFailure() {
        //password is wrong
        RegisterResult result = registerUser();
        userService.logout(new LogoutRequest(result.authToken()));
        Assertions.assertThrows(ResponseException.class,() ->
                userService.login(new LoginRequest(result.username(),"wrongpassword")));
        clearService.clear();
    }

    //GameService tests

    @Test
    @Order(7)
    @DisplayName("Successful Create Game")
    public void createGameSuccess() {
        RegisterResult result = registerUser();
        CreateGameResult gameResult = Assertions.assertDoesNotThrow( () ->
                createGame(result.authToken()));
        Assertions.assertTrue(gameResult.gameID()>0);
        clearService.clear();
    }
    @Test
    @Order(8)
    @DisplayName("Unsuccessful Create Game")
    public void createGameFailure() {
        //no game name provided
        RegisterResult result = registerUser();
        Assertions.assertThrows(ResponseException.class,() ->
                gameService.createGame(new CreateGameRequest("",result.authToken())));
        clearService.clear();
    }

    @Test
    @Order(9)
    @DisplayName("Successful Join Game")
    public void joinGameSuccess() {
        RegisterResult result = registerUser();
        CreateGameResult gameResult = createGame(result.authToken());
        JoinGameRequest joinReq = new JoinGameRequest(ChessGame.TeamColor.WHITE,gameResult.gameID(),result.authToken());
        Assertions.assertDoesNotThrow(() ->
                gameService.joinGame(joinReq));
        clearService.clear();
    }
    @Test
    @Order(10)
    @DisplayName("Unsuccessful Join Game")
    public void joinGameFailure() {
        //no player color provided
        RegisterResult result = registerUser();
        CreateGameResult gameResult = createGame(result.authToken());
        Assertions.assertThrows(ResponseException.class,() ->
                gameService.joinGame(new JoinGameRequest(null,gameResult.gameID(),result.authToken())));
        clearService.clear();
    }
    @Test
    @Order(11)
    @DisplayName("Successful List Games")
    public void listGameSuccess() {
        RegisterResult result = registerUser();
        CreateGameResult gameResult = createGame(result.authToken());
        ListGamesResult listResult = Assertions.assertDoesNotThrow( () ->
                gameService.listGames(new ListGamesRequest(result.authToken())));
        ArrayList<GameData> expectedResult = new ArrayList<>();
        expectedResult.add(new GameData(gameResult.gameID(),null,null,"testGame",new ChessGame()));
        Assertions.assertEquals(listResult.games(),expectedResult);
        clearService.clear();
    }
    @Test
    @Order(12)
    @DisplayName("Unsuccessful List Games")
    public void listGameFailure() {
        RegisterResult result = registerUser();
        GameService gameService = new GameService();
        Assertions.assertThrows(ResponseException.class,() ->
                gameService.listGames(new ListGamesRequest("")));
        clearService.clear();
    }
    @Test
    @Order(13)
    @DisplayName("Successful Clear")
    public void clearSuccess() {
        RegisterResult result = registerUser();
        Assertions.assertDoesNotThrow(()->new ClearService().clear());
        Assertions.assertThrows(ResponseException.class,()->
                userService.login(new LoginRequest(result.username(),"testpassword")));
    }

    private RegisterResult registerUser() {
        RegisterRequest request = new RegisterRequest("testusername","testpassword","testemail");
        return userService.register(request);
    }
    private CreateGameResult createGame(String authToken){
        return gameService.createGame(new CreateGameRequest("testGame",authToken));
    }
}
