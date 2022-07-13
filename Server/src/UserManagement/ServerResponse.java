package UserManagement;

import java.io.Serializable;

public class ServerResponse implements Serializable {
    private String data;
    private ResponseType type;
    public ServerResponse(String data, ResponseType type) {
        this.data = data;
        this.type = type;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public ResponseType getType() {
        return type;
    }
    public void setType(ResponseType type) {
        this.type = type;
    }
    public boolean isOk(){
        return type==ResponseType.OK;
    }
}
