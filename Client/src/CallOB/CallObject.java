package CallOB;

public class CallObject {
    private String callID;
    private long timeStarted;

    public CallObject(String callID, long timeStarted) {
        this.callID = callID;
        this.timeStarted = timeStarted;
    }

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }
}
