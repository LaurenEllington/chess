package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import resultrequest.*;
import service.ResponseException;

public class UserServiceTests {
    @Test
    @DisplayName("Valid User Registration")
    public void registerSuccess() {
        RegisterRequest request = new RegisterRequest("testusername","testpassword","testemail");
        UserService userService = new UserService();
        RegisterResult result = userService.register(request);
        Assertions.assertNotNull(result.username(),"No username was returned in result");
        Assertions.assertEquals(request.username(),result.username(),
                "Requested username is not the same as username returned");
        Assertions.assertNotNull(result.authToken(),"No authToken was returned");
    }
    @Test
    @DisplayName("Invalid User Registration")
    public void registerFailure() {
        //username is already taken
        RegisterRequest request = new RegisterRequest("testusername2","testpassword2","testemail2");
        UserService userService = new UserService();
        RegisterResult result = userService.register(request);
        Assertions.assertThrows(ResponseException.class,() ->
                userService.register(request));
    }
    @Test
    @DisplayName("Valid User Logout")
    public void logoutSuccess() {
        RegisterRequest request = new RegisterRequest("testusername3","testpassword3","testemail3");
        UserService userService = new UserService();
        RegisterResult result = userService.register(request);
        LogoutRequest request2 = new LogoutRequest(result.authToken());
        Assertions.assertDoesNotThrow( () -> userService.logout(request2));
    }
    @Test
    @DisplayName("Invalid User Logout")
    public void logoutFailure() {
        //no authToken
        RegisterRequest request = new RegisterRequest("testusername4","testpassword4","testemail4");
        UserService userService = new UserService();
        RegisterResult result = userService.register(request);
        LogoutRequest request2 = new LogoutRequest("");
        Assertions.assertThrows(ResponseException.class,() ->
                userService.logout(request2));
    }
    @Test
    @DisplayName("Valid User Login")
    public void loginSuccess() {
        LoginRequest request = new LoginRequest("testusername3","testpassword3");
        UserService userService = new UserService();
        LoginResult result = userService.login(request);
        Assertions.assertEquals(request.username(),result.username(),
                "Requested username to log into is not the same as username returned");
        Assertions.assertNotNull(result.authToken(),"No authToken was returned");
    }
    @Test
    @DisplayName("Invalid User Login")
    public void loginFailure() {
        //password is wrong
        LoginRequest request = new LoginRequest("testusername","wrongPassword");
        UserService userService = new UserService();
        Assertions.assertThrows(ResponseException.class,() ->
                userService.login(request));
    }
}
