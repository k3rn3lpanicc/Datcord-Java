package UserManagement;

import java.io.Serializable;
import java.util.Objects;

public abstract class Message implements Serializable {
    private String date;
    private String fromID;
    private String messageID;
    private String replyID;
    private String fromUsername;
    private String chat_id;
    public Message(String date, String fromID, String messageID, String replyID,String fromUsername,String chat_id) {
        this.date = date;
        this.fromID = fromID;
        this.messageID = messageID;
        this.replyID = replyID;
        this.fromUsername = fromUsername;
        this.chat_id = chat_id;
    }



    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return getDate().equals(message.getDate()) &&
                getFromID().equals(message.getFromID()) &&
                getMessageID().equals(message.getMessageID()) &&
                getReplyID().equals(message.getReplyID()) &&
                getFromUsername().equals(message.getFromUsername()) &&
                getChat_id().equals(message.getChat_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getFromID(), getMessageID(), getReplyID(), getFromUsername(), getChat_id());
    }
}
