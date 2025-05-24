package model;

/*public class UserData { //name, email address, password
    private String username; //make final
    private String email;
    private String password;
    public UserData(String username, String email, String password) {
        this.username = username;

    }
}*/
public record UserData(String username, String password, String email) {}
