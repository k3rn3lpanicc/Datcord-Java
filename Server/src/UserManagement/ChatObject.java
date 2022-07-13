package UserManagement;

public class ChatObject {
    private String id;
    private String name;
    private String type;
    private String serverID;
    private String members;
    public ChatObject(String id, String name, String type, String serverID, String members) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.serverID = serverID;
        this.members = members;
    }
    private Message pinnedMessage;

    public Message getPinnedMessage() {
        return pinnedMessage;
    }

    public void setPinnedMessage(Message pinnedMessage) {
        this.pinnedMessage = pinnedMessage;
    }

    public ChatObject(){}
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getServerID() {
        return serverID;
    }

    public String getMembers() {
        return members;
    }
}
