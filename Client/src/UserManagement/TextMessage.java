package UserManagement;

import java.io.Serializable;

public class TextMessage extends Message implements Serializable {
    private String content;
    public TextMessage(String content, String date, String fromID, String messageID, String replyID,String fromUsername,String chat_id) {
        super(date, fromID, messageID, replyID,fromUsername,chat_id);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
