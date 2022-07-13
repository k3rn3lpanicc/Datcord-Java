package CallService;

import java.io.Serializable;

public class CallPacket implements Serializable {
    private byte[] voiceData;
    private byte[] screenImageDate;
    private String token;
    private String callID;
    public CallPacket(byte[] voiceData,byte[] screenImageDate, String token,String callID) {
        this.voiceData = voiceData;
        this.token = token;
        this.callID = callID;
        this.screenImageDate = screenImageDate;
    }

    public byte[] getScreenImageDate() {
        return screenImageDate;
    }

    public void setScreenImageDate(byte[] screenImageDate) {
        this.screenImageDate = screenImageDate;
    }

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    public byte[] getVoiceData() {
        return voiceData;
    }

    public void setVoiceData(byte[] voiceData) {
        this.voiceData = voiceData;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
