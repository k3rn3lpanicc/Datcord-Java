package CallOB;

import java.io.Serializable;

public class CallRequest implements Serializable {
    private String username;
    private String callID;
    public CallRequest(String username,String callID) {
        this.username = username;
        this.callID = callID;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getCallID() {
        return callID;
    }
    public void setCallID(String callID) {
        this.callID = callID;
    }
}
