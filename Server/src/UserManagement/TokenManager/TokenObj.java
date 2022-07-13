package UserManagement.TokenManager;

public class TokenObj {
    private String token;
    private String username;
    private String date;
    private String publicKey;
    private String symmetricKey;

    public TokenObj(String token, String username, String date, String publicKey, String symmetricKey) {
        this.token = token;
        this.username = username;
        this.date = date;
        this.publicKey = publicKey;
        this.symmetricKey = symmetricKey;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    @Override
    public String toString() {
        return "TokenObj{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
