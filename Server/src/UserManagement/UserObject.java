package UserManagement;

import java.io.Serializable;
import java.util.ArrayList;

public class UserObject implements Serializable {
    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private String phoneNumber;
    private UserStatus status;
    private ArrayList<String> friends = new ArrayList<>();
    public UserObject(int id,String username, String passwordHash, String email) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }
    public UserObject(){
    }
    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public String getFriends(){
        if(friends.size()!=0) {
            String result = friends.get(0);
            for (int i = 1; i < friends.size(); i++)
                result += "," + friends.get(i);
        return result;
        }
        return "";
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
