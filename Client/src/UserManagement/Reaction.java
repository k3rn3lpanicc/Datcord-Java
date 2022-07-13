package UserManagement;

import java.io.Serializable;

public class Reaction implements Serializable {
    private String chat_id;
    private String message_id;
    private String from_id;
    private String reaction;

    public Reaction(String chat_id, String message_id, String from_id, String reaction) {
        this.chat_id = chat_id;
        this.message_id = message_id;
        this.from_id = from_id;
        this.reaction = reaction;
    }
    public Reaction(){}

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
