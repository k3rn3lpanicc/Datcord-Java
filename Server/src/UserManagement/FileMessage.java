package UserManagement;

import java.io.Serializable;

public class FileMessage extends Message implements Serializable {
    private String fileID;
    private String filename;
    public FileMessage(String filename,String fileID , String date, String fromID, String messageID, String replyID,String fromUsername,String chat_id) {
        super(date, fromID, messageID, replyID,fromUsername,chat_id);
        this.fileID = fileID;
        this.filename = filename;
    }
    public String getFileID() {
        return fileID;
    }
    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
